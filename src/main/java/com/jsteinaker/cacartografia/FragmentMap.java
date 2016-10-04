package com.jsteinaker.cacartografia;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.InputStreamReader;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.util.Hashtable;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class FragmentMap extends Fragment {
	
	/* UI */
	private View fragmentView;
	private MapView mapView;
	private FloatingActionButton locationButton;
	private FloatingActionButton directionsButton;

	/* Data */
	private static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoianN0ZWluYWtlciIsImEiOiI4Zjc4YTFiNzkwMWFiYmFhZTVhNjJjODdkZGM5YzM1NiJ9.opMzYPAFV5uhK3f_UIqKcQ";
	private MapboxMap map;
	private LocationServices locationServices;
	private DatabaseReference database;
	private DUALCMarker newMarker;
	private Bundle instanceStateCopy;
	private FirebaseUser user;
	private long nextMarkerId;
	private Hashtable<String, Long> idTable;
	private Polyline route;
	private Database databaseRef;
	private ArrayAdapter<Point> autocompleteMarkerList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instanceStateCopy = savedInstanceState;
		nextMarkerId = 0;
		idTable = new Hashtable<String, Long>();
 		autocompleteMarkerList = new ArrayAdapter<Point>(getActivity(), android.R.layout.simple_list_item_1);
		databaseRef = new Database() {
			@Override
			public void onMarkerAdded(DataSnapshot dataSnapshot) {
				Point marker = dataSnapshot.getValue(Point.class);
				drawNewMarker(marker, dataSnapshot.getKey());
			}

			@Override
			public void onMarkerDeleted(DataSnapshot dataSnapshot) {
				removeDeletedMarker(dataSnapshot.getKey());
			}

			@Override
			public void onMarkerDataChanged(DataSnapshot dataSnapshot) {
				reloadSearchData(dataSnapshot);
			}
		};
				
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (fragmentView == null)
		{
			setHasOptionsMenu(true);
			fragmentView = inflater.inflate(R.layout.map, container, false);
			mapView = (MapView) fragmentView.findViewById(R.id.mapView);
			mapView.onCreate(savedInstanceState);
			setupMapView();
			locationButton = (FloatingActionButton) fragmentView.findViewById(R.id.locationButton);
			directionsButton = (FloatingActionButton) fragmentView.findViewById(R.id.directions_button);
			setupButtons();
			setupSearchWidget();
		}

		// Modificaciones en la AppBar
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		return fragmentView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
			mapView.onDestroy();
		}
    }

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		if (mapView != null) {
        	mapView.onSaveInstanceState(outState);
		}
    }
	
	/* Prepara el mapa */
	private void setupMapView() {
		newMarker = null;
		database = FirebaseDatabase.getInstance().getReference();
		mapView.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(MapboxMap mapboxMap) {
				map = mapboxMap;
				// Icono a partir de drawable
				IconFactory iconFactory = IconFactory.getInstance(getActivity());
				Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_new_marker);
				final Icon newMarkerIcon = iconFactory.fromDrawable(iconDrawable);
				/* Y el listener para los clicks "largos" que añaden el lugar */
				map.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
					@Override
					public void onMapLongClick(LatLng point) {
						if (newMarker != null)
						{
							map.removeMarker(newMarker);
						}
						newMarker = (DUALCMarker) map.addMarker(new DUALCMarkerOptions()
								.position(point)
								.icon(newMarkerIcon));
					}
				});
				/* También un listener para que los clicks comunes "limpien" el nuevo marcador */
				map.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
					@Override
					public void onMapClick(LatLng point) {
						if (newMarker != null)
						{
							map.removeMarker(newMarker);
						}
						newMarker = null;
						/* Y de paso, ocultamos el botón de direcciones si no
						 * hay marcador seleccionado. */
						directionsButton.setVisibility(View.GONE);
					}
				});
				/* Y un listener para verificar cuando el usuario clickea sobre el nuevo marcador */
				map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
					@Override
					public boolean onMarkerClick(Marker marker) {
						if (marker == newMarker)
						{
							user = FirebaseAuth.getInstance().getCurrentUser();
							if (user != null) {
								((DUALC)getActivity()).loadAddNewMarkerFragment(marker.getPosition(), nextMarkerId);
								/* Quitamos del mapa el marcador provisional luego de lanzar el otro fragmento */
								map.removeMarker(newMarker);
								newMarker = null;
								return true;
							}
							else {
								newMarker.setTitle(getString(R.string.login_needed));
								newMarker.setSnippet(getString(R.string.login_needed_message));
							}
						}
						return false;
					}
				});

				/* Un adaptador para modificar las InfoWindow mostradas*/
				map.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
					@Override
					public View getInfoWindow(Marker marker) {
						DUALCInfoWindow infoWindow = (DUALCInfoWindow) LayoutInflater
							.from(getActivity())
							.inflate(R.layout.info_window, null);
						/* Altura máxima de InfoWindow relativa a la altura del
						 * mapa en pantalla */
						infoWindow.setMaxHeight(mapView.getHeight());
						TextView tv = (TextView) infoWindow.findViewById(R.id.infowindow_title);
						tv.setText(marker.getTitle());
						tv = (TextView) infoWindow.findViewById(R.id.infowindow_description);
						tv.setText(marker.getSnippet());
						/* Se puede hacer scroll en la descripción */
						tv.setMovementMethod(new ScrollingMovementMethod());
						ImageButton btn = (ImageButton) infoWindow.findViewById(R.id.infowindow_editmarker);
						/* Cast explícito necesario para poder usar getOwner */
						DUALCMarker dualcMarker = (DUALCMarker) marker;
						user = FirebaseAuth.getInstance().getCurrentUser();
						/* Chequeo del usuario actual y el dueño del marcador,
						 * si es el mismo puede editarlo */
						if (user != null) {
							String owner = user.getEmail();
							if (dualcMarker.getOwner().equals(owner)) {
								btn.setVisibility(View.VISIBLE);
								final DUALCMarker markerCopy = dualcMarker;
								btn.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										Long dualcId = markerCopy.getDualcId();
										((DUALC)getActivity()).loadEditMarkerFragment(markerCopy, dualcId);
									}
								});
							}
						}
						/* Hacemos visible al botón de direcciones */
						directionsButton.setVisibility(View.VISIBLE);
						return infoWindow;
					}
				});

				/* Inicializamos los Location Services */
				locationServices = LocationServices.getLocationServices(getActivity());

				/* Cargamos marcadores */
				databaseRef.setUpListeners();

			}
		});
	}

	/* Añade los Floating Action Buttons */
	private void setupButtons() {
		locationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				locateUser();
			}
		});
		
		directionsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Location location = locationServices.getLastLocation();
				Marker marker = map.getSelectedMarkers().get(0);
				Route route = new Route(new LatLng(location), new LatLng(marker.getPosition())) {
					@Override
					public void onRouteReady() {
						drawRoute(waypoints);
					}
				};
				route.calculate(Route.WALKING);
			}
		});
	}

	/* Mueve la cámara a la ubicación elegida */
	public void updateCamera(LatLng location) {
		map.setCameraPosition(new CameraPosition.Builder()
			.target(location)
			.zoom(14)
			.build()); 
	}

	/* Obtiene ubicación por GPS y mueve la cámara */
	public void locateUser() {
		Location location = locationServices.getLastLocation();
			// Mover mapa
			if (location != null) {
				updateCamera(new LatLng(location));
			}
			
		map.setMyLocationEnabled(true);
	}

	/* Cuadro de búsqueda */
	public void setupSearchWidget() {
		AutoCompleteTextView searchWidget = (AutoCompleteTextView) fragmentView.findViewById(R.id.search_widget);
		searchWidget.setAdapter(autocompleteMarkerList);
		searchWidget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Point result = (Point) adapterView.getItemAtPosition(position);
			}
		});
	}

	/* Refresca los datos de búsqueda en caso de cambios */
	public void reloadSearchData(DataSnapshot dataSnapshot) {
		for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
			Point suggestion = suggestionSnapshot.getValue(Point.class);
			autocompleteMarkerList.add(suggestion);
		}
	}


	public void drawNewMarker(Point point, String userId) {
		Properties properties = point.getProperties();
		Geometry geometry = point.getGeometry();
		DUALCMarker mapMarker = (DUALCMarker) map.addMarker(new DUALCMarkerOptions()
				.title(properties.getTitle())
				.snippet(properties.getDescription())
				.position(geometry.getCoordinatesInLatLng())
				.owner(properties.getOwner())
				.dualcId(point.getId()));
		idTable.put(userId, mapMarker.getId());
		nextMarkerId = point.getId() + 1;
	}

	public void removeDeletedMarker(String userId) {
		map.removeAnnotation(idTable.get(userId));
		idTable.remove(userId);
	}

	public void drawRoute(LatLng[] waypoints) {
		if (route != null)
			map.removePolyline(route);
		route = map.addPolyline(new PolylineOptions()
				.add(waypoints)
				.width(5));
	}

	public void deselectMarkers() {
		map.deselectMarkers();
	}

}
