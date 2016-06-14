package com.jsteinaker.cacartografia;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public class DUALC extends AppCompatActivity
{
    private MapView mapView;
	private MapboxMap map;

	FloatingActionButton locationButton;
	FloatingActionButton markerButton;
	LocationServices locationServices;
	
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		locationServices = LocationServices.getLocationServices(DUALC.this);

		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);
		mapView.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(MapboxMap mapboxMap) {
				map = mapboxMap;
				/** Cuando el mapa está listo, movemos la cámara al punto actual. **/
				locateUser();
			}
		});

		/** Creamos el botón flotante para geolocalización. */ 
		/**	y el listener para cuando es presionado */

		locationButton = (FloatingActionButton) findViewById(R.id.locationButton);
		locationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				locateUser();
			}
		});

		/** Botón flotante para marcador, y listener **/

		markerButton = (FloatingActionButton) findViewById(R.id.markerButton);
		markerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				putMarker(locationServices.getLastLocation());
			}
		});

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
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

	// Obtiene ubicación por GPS y mueve la cámara
	public void locateUser() {
		locationServices.addLocationListener(new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					// Mover mapa
					map.setCameraPosition(new CameraPosition.Builder()
						.target(new LatLng(location))
						.zoom(16)
						.build());
				}
			}
		});
		map.setMyLocationEnabled(true);
	}

	// Agrega marcador en el punto seleccionado
	public void putMarker(Location location)
	{
		// Creamos ícono
		IconFactory iconFactory = IconFactory.getInstance(DUALC.this);
		Drawable iconDrawable = ContextCompat.getDrawable(DUALC.this, R.drawable.toilet);
		Icon markerIcon = iconFactory.fromDrawable(iconDrawable);

		// Agregamos el marcador
		map.addMarker(new MarkerOptions().position(new LatLng(location)).icon(markerIcon));
	}
}
