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
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.InputStreamReader;
import java.lang.Exception;
import java.lang.StringBuilder;
import java.util.Hashtable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.ValueEventListener;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class FragmentMap extends Fragment {
	
	/* UI */
	View fragmentView;
	private MapView mapView;
	private MapboxMap map;
	FloatingActionButton locationButton;

	/* Data */
	LocationServices locationServices;
	private DatabaseReference database;
	DUALCMarker newMarker;
	Bundle instanceStateCopy;
	FirebaseUser user;
	long nextMarkerId;
	Hashtable<String, Long> idTable;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instanceStateCopy = savedInstanceState;
		nextMarkerId = 0;
		idTable = new Hashtable<String, Long>();
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
			setupButtons();
		}

		// Modificaciones en la AppBar
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		return fragmentView;
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
								((DUALC)getActivity()).loadAddNewMarkerFragment(marker.getPosition(), ((Long)nextMarkerId).toString());
								return true;
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
										((DUALC)getActivity()).loadEditMarkerFragment(markerCopy, ((Long)markerCopy.getId()).toString());
									}
								});
							}
						}
						return infoWindow;
					}
				});

				/* Inicializamos los Location Services */
				locationServices = LocationServices.getLocationServices(getActivity());

				/* Cargamos marcadores */
				loadMarkers();

				}
		});
	}

	// Añade los Floating Action Buttons
	private void setupButtons() {
		locationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				locateUser();
			}
		});
	}
	
	// Obtiene ubicación por GPS y mueve la cámara
	public void locateUser() {
		Location location = locationServices.getLastLocation();
			if (location != null) {
			// Mover mapa
			map.setCameraPosition(new CameraPosition.Builder()
				.target(new LatLng(location))
				.zoom(14)
				.build()); 
			}
			
		map.setMyLocationEnabled(true);
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
	
	public void loadMarkers() {

		// Icono a partir de drawable
		//IconFactory iconFactory = IconFactory.getInstance(getActivity());
		//Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.toilet);
		//final Icon markerIcon = iconFactory.fromDrawable(iconDrawable);

		// Empieza lo bueno
		/* **************************************************************
		database.child("features").addListenerForSingleValueEvent(
			new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot marker : dataSnapshot.getChildren())
				{
					LatLng location = new LatLng(marker.child("geometry").
						child("coordinates").child("1").getValue(Double.class),
						marker.child("geometry").child("coordinates").
						child("0").getValue(Double.class));
					
					map.addMarker(new MarkerOptions().
						title(marker.child("properties").child("title").
							getValue(String.class)).
						snippet(marker.child("properties").child("description").
							getValue(String.class)).position(location));
					
				}
				nextMarkerId = dataSnapshot.getChildrenCount();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.w("DUALC", "getMarkers:onCancelled", databaseError.toException());
			}

		});
		*************************************************************** */

		/* Ponemos listeners para responder a los cambios que puedan ocurrir en
		 * la base de datos (nuevo marcador, marcador suprimido, etc.) */
		database.child("features").addChildEventListener(new ChildEventListener() {
			/* Nuevo marcador */
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
				Point marker = dataSnapshot.getValue(Point.class);
				drawNewMarker(marker, dataSnapshot.getKey());
			}

			/* Marcador modificado */
			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
			}

			/* Marcador movido */
			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
			}

			/* Marcador eliminado */
			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {
				removeDeletedMarker(dataSnapshot.getKey());
			}
			
			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.w("DUALC", "ChildEventListener:onCancelled", databaseError.toException());
			}
		});
	}

	public void addMarker(Point marker, String id) {
		database.child("features").child(id).setValue(marker);
		/* Como el marcador ha quedado añadido al mapa, quitamos el marcador
		 * provisional del mapa y limpiamos la referencia a newMarker para 
		 * que al clickearlo no nos lleve a la pantalla de agregar marcador. */
		map.removeMarker(newMarker);
		newMarker = null;
	}

	public void editMarker(Point marker, String id) {
		database.child("features").child(id).setValue(marker);
	}

	public void drawNewMarker(Point marker, String userId) {
		Properties properties = marker.getProperties();
		Geometry geometry = marker.getGeometry();
		DUALCMarker mapMarker = (DUALCMarker) map.addMarker(new DUALCMarkerOptions()
				.title(properties.getTitle())
				.snippet(properties.getDescription())
				.position(geometry.getCoordinatesInLatLng())
				.owner(properties.getOwner()));
		idTable.put(userId, mapMarker.getId());
		nextMarkerId++;
	}

	public void removeDeletedMarker(String userId) {
		map.removeAnnotation(idTable.get(userId));
		idTable.remove(userId);
	}
}
