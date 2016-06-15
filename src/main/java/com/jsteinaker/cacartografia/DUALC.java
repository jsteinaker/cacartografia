package com.jsteinaker.cacartografia;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.InputStreamReader;
import java.lang.Exception;
import java.lang.StringBuilder;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);
		setupMapView();

		locationButton = (FloatingActionButton) findViewById(R.id.locationButton);
		setupButtons();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
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
						.zoom(14)
						.build());
				}
			}
		});
		map.setMyLocationEnabled(true);
	}

	// Prepara el mapa
	private void setupMapView() {
		mapView.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(MapboxMap mapboxMap) {
				map = mapboxMap;
				/** Cuando el mapa está listo, movemos la cámara al punto actual. **/
				//locateUser();
			}
		});

		/** Y creamos los marcadores, llamando a otra clase para
		 * cargar el GeoJSON */
		new LoadMarkers().execute();
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

	private class LoadMarkers extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... args) {
			final StringBuilder json = new StringBuilder();
			try
			{
				InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open("toilets.json"));
				int read;
				char[] buff = new char[1024];
				while ((read = inputStreamReader.read(buff)) != -1)
						{
							json.append(buff, 0, read);
						}
			} catch (Exception e) {
				Log.e("DUALC", "Exception loading GeoJSON: " + e.toString());
			}
	
			return json.toString();
		}
	
		@Override
		protected void onPostExecute(String json) {
	
			// Creamos ícono a partir del drawable
			IconFactory iconFactory = IconFactory.getInstance(DUALC.this);
			Drawable iconDrawable = ContextCompat.getDrawable(DUALC.this, R.drawable.toilet);
			Icon markerIcon = iconFactory.fromDrawable(iconDrawable);
			
			try
			{
				Log.w("Sarasa", "Arrancando");
				JSONObject intermediate = new JSONObject(json);
				JSONArray features = intermediate.getJSONArray("features");
				Log.w("Sarasa", "Array cargado");
				for (int i = 0; i < features.length(); i++)
				{
					JSONObject feature = features.getJSONObject(i);
					JSONObject geometry = feature.getJSONObject("geometry");
					JSONArray coordinates = geometry.getJSONArray("coordinates");
					LatLng location = new LatLng(coordinates.getDouble(1),coordinates.getDouble(0));
	
					// Crea el marcador
					JSONObject properties = feature.getJSONObject("properties");
					map.addMarker(new MarkerOptions().title(properties.getString("title")).snippet(properties.getString("description")).position(location).icon(markerIcon));
				}
			} catch (JSONException e) {
				Log.e("DUALC", "Error processing JSON: " + e);
			}
		}
	
	}
}
