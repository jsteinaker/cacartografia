package com.jsteinaker.cacartografia;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import java.io.InputStreamReader;
import java.lang.Exception;
import java.lang.StringBuilder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
	
	View fragmentView;
	private MapView mapView;
	private MapboxMap map;
	FloatingActionButton locationButton;
	LocationServices locationServices;
	private DatabaseReference database;
	Marker newMarker;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (fragmentView == null)
		{
			fragmentView = inflater.inflate(R.layout.map, container, false);
			mapView = (MapView) fragmentView.findViewById(R.id.mapView);
			mapView.onCreate(savedInstanceState);
			setupMapView();
			locationButton = (FloatingActionButton) fragmentView.findViewById(R.id.locationButton);
			setupButtons();
		}

		return fragmentView;
	}

	// Prepara el mapa
	private void setupMapView() {
		newMarker = null;
		mapView.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(MapboxMap mapboxMap) {
				map = mapboxMap;
				// Icono a partir de drawable
				IconFactory iconFactory = IconFactory.getInstance(getActivity());
				Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_add_marker);
				final Icon addMarkerIcon = iconFactory.fromDrawable(iconDrawable);
				/* Y el listener para los clicks "largos" que añaden el lugar */
				map.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
					@Override
					public void onMapLongClick(LatLng point) {
						if (newMarker != null)
						{
							map.removeMarker(newMarker);
						}
						newMarker = map.addMarker(new MarkerOptions().position(point));
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
					}
				});
				/* Y un listener para verificar cuando el usuario clickea sobre el nuevo marcador */
				map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
					@Override
					public boolean onMarkerClick(Marker marker) {
						if (marker == newMarker)
						{
							((DUALC)getActivity()).loadAddNewMarkerFragment(marker.getPosition());
							return true;
						}
						return false;
					}
				});

				/* Inicializamos los Location Services */
				locationServices = LocationServices.getLocationServices(getActivity());

				/* Cargamos marcadores */
				LoadMarkers();

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
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
	
	public void LoadMarkers() {
		database = FirebaseDatabase.getInstance().getReference();

		// Icono a partir de drawable
		//IconFactory iconFactory = IconFactory.getInstance(getActivity());
		//Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.toilet);
		//final Icon markerIcon = iconFactory.fromDrawable(iconDrawable);

		// Empieza lo bueno
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
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.w("DUALC", "getMarker:onCancelled", databaseError.toException());
			}

		});
	}
}
