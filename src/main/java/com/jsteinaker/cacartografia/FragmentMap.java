package com.jsteinaker.cacartografia;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import com.mapbox.services.android.telemetry.location.LocationEngine;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class FragmentMap extends BaseFragment {
	
	/* UI */
	private View fragmentView;
	private MapView mapView;
	private FloatingActionButton locationButton;
	private FloatingActionButton directionsButton;
	private FloatingActionButton editMarkerButton;
	private SlidingUpPanelLayout slidingPanel;
	private AutoCompleteTextView searchBox;

	/* Data */
	private MapboxMap map;
	private LocationEngine locationEngine;
	private DatabaseReference database;
	private DUALCMarker newMarker;
	private FirebaseUser user;
	private Hashtable<String, Long> idTable;
	private Hashtable<Long, String> reverseIdTable;
	private Polyline route;
	private Database databaseRef;
	private ArrayAdapter<Point> autocompleteMarkerList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Location Engine
		locationEngine = new LocationSource(getActivity());
		locationEngine.activate();

		idTable = new Hashtable<String, Long>();
		reverseIdTable = new Hashtable<Long, String>();
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
			editMarkerButton = (FloatingActionButton) fragmentView.findViewById(R.id.edit_marker_button);
			setupButtons();
			slidingPanel = (SlidingUpPanelLayout) fragmentView.findViewById(R.id.sliding_layout);
			searchBox = (AutoCompleteTextView) fragmentView.findViewById(R.id.search_box);
			setupSearchBox();
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
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() == R.id.search) {
			showSearchBox();
			return true;
		}
		return super.onOptionsItemSelected(menuItem);
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
				final Icon newMarkerIcon = iconFactory.fromResource(R.drawable.ic_new_marker);
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
				/* Click en una parte vacía del mapa */
				map.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
					@Override
					public void onMapClick(LatLng point) {
						// Elimina el ícono de crear nuevo marcador, si estuviera
						if (newMarker != null)
						{
							map.removeMarker(newMarker);
						}
						newMarker = null;
				 		// Oculta los controles que no correspondan
						directionsButton.setVisibility(View.GONE);
						infoPanelDown();
						hideSearchBox();
					}
				});
				/* Y un listener para verificar cuando el usuario clickea sobre el nuevo marcador */
				map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
					@Override
					public boolean onMarkerClick(Marker marker) {
						if (marker == newMarker) {
							user = FirebaseAuth.getInstance().getCurrentUser();
							if (user != null) {
								((DUALC)getActivity()).loadAddNewMarkerFragment(marker.getPosition());
								/* Quitamos del mapa el marcador provisional luego de lanzar el otro fragmento */
								map.removeMarker(newMarker);
								newMarker = null;
								return true;
							}
							else {
								newMarker.setTitle(getString(R.string.login_needed));
								newMarker.setSnippet(getString(R.string.login_needed_message));
								return false;
							}
						}
						else {
							TextView textView = (TextView) fragmentView.findViewById(R.id.marker_title);
							textView.setText(marker.getTitle());
							textView = (TextView) fragmentView.findViewById(R.id.marker_description);
							textView.setText(marker.getSnippet());
							infoPanelUp();
							// Hacemos visible al botón de direcciones
							directionsButton.setVisibility(View.VISIBLE);
							// Cast explícito necesario para poder usar getOwner
							DUALCMarker dualcMarker = (DUALCMarker) marker;
							user = FirebaseAuth.getInstance().getCurrentUser();
							// Chequeo del usuario actual y el dueño del marcador,
							// si es el mismo puede editarlo
							if (user != null) {
								String owner = user.getEmail();
								if (dualcMarker.getOwner().equals(owner)) {
									editMarkerButton.setVisibility(View.VISIBLE);
								}
							}
						}
						return true;
					}
				});
				
				/* Cargamos marcadores */
				databaseRef.setUpListeners();

				/* FIXME: el panel debería iniciar oculto, y no bajar luego
				 * de cargado el mapa */
				infoPanelDown();

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
				Location location = locationEngine.getLastLocation();
				Marker marker = map.getSelectedMarkers().get(0);
				Route route = new Route(new LatLng(location), new LatLng(marker.getPosition())) {
					@Override
					public void onRouteReady() {
						drawRoute(waypoints);
						Toast.makeText(getActivity(), street, Toast.LENGTH_SHORT).show();
					}
				};
				route.calculate(Route.WALKING);
			}
		});

		editMarkerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final DUALCMarker markerCopy = ((DUALCMarker) map.getSelectedMarkers().get(0));
				String id = reverseIdTable.get(markerCopy.getId());
				((DUALC)getActivity()).loadEditMarkerFragment(markerCopy, id);
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
		Location location = locationEngine.getLastLocation();
			// Mover mapa
			if (location != null) {
				updateCamera(new LatLng(location));
			}
			
		map.setMyLocationEnabled(true);
	}

	/* Cuadro de búsqueda */
	public void setupSearchBox() {
		searchBox.setAdapter(autocompleteMarkerList);
		searchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				Point result = (Point) adapterView.getItemAtPosition(position);
				updateCamera(result.getGeometry().getCoordinatesInLatLng());
				/* FIXME
				 * Selecciona el marcador (consigue el ID mediante hashtable)
				 * Para seleccionar el marcador, hay que:
				 * Conseguir el ID del objeto Point
				 * Transformarlo a String (es Long)
				 * Conseguir el ID interno de Mapbox mediante el hashtable
				 * Conseguir referencia al marcador mediante getAnnotation()
				 * Castear esa referencia al tipo Marker
				 * Finalmente, llamar a la función de selección
				 * Evidentemente, hay que repensar el sistema de referencias
				 * y IDs, o, mejor dicho, las estructuras de datos por
				 * completo. */
				map.selectMarker((Marker)map.getAnnotation(idTable.get(result.getId())));
				Utils.hideKeyboard(getActivity());
				searchBox.setText(null);
				hideSearchBox();
			}
		});
	}

	/* Refresca los datos de búsqueda en caso de cambios */
	public void reloadSearchData(DataSnapshot dataSnapshot) {
		for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {
			Point suggestion = suggestionSnapshot.getValue(Point.class);
			// Mete a la fuerza el key de Firebase
			suggestion.setId(suggestionSnapshot.getKey());
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
				.owner(properties.getOwner()));
		idTable.put(userId, mapMarker.getId());
		reverseIdTable.put(mapMarker.getId(), userId);
	}

	public void removeDeletedMarker(String userId) {
		map.removeAnnotation(idTable.get(userId));
		reverseIdTable.remove(idTable.get(userId));
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
		infoPanelDown();
	}

	/* Sube el panel de información */
	public void infoPanelUp() {
		slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
	}

	/* Baja el panel de información */
	public void infoPanelDown() {
		slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
		editMarkerButton.setVisibility(View.GONE);
	}
	
	/* Baja la caja de búsqueda */
	public void showSearchBox() {
		Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
		View searchBoxLayout = (View) fragmentView.findViewById(R.id.search_box_layout);
		searchBoxLayout.startAnimation(anim);
		searchBox.setVisibility(View.VISIBLE);
	}

	/* Sube la caja de búsqueda */
	public void hideSearchBox() {
		Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out);
		View searchBoxLayout = (View) fragmentView.findViewById(R.id.search_box_layout);
		searchBoxLayout.startAnimation(anim);
		searchBox.setVisibility(View.GONE);
	}

	/* Control de la tecla "back" */
	@Override
	public boolean onBackPressed() {
		if (slidingPanel != null && slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
			slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
			return true;
		}
		return false;
	}


}
