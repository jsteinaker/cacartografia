package com.jsteinaker.cacartografia;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class DUALC extends AppCompatActivity implements OnFragmentInteractionListener
{
	FragmentManager fragmentManager;
	FragmentMap fragmentMap;
	
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		fragmentManager = getSupportFragmentManager();
			
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (savedInstanceState == null)
		{
			fragmentMap = new FragmentMap();
			fragmentManager.beginTransaction().add(R.id.fragment_frame, fragmentMap, "MAIN_MAP").commit();
		}
		else
		{
			fragmentMap = (FragmentMap) fragmentManager.findFragmentByTag("MAIN_MAP");
		}

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		// Recupera el fragmento anterior si se presiona Home (Back)
		if (menuItem.getItemId() == android.R.id.home)
		{
			getSupportFragmentManager().popBackStack();
		}
		else if (menuItem.getItemId() == R.id.about) {
			AboutFragment aboutFragment = new AboutFragment();
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_frame, aboutFragment).addToBackStack(null).commit();
		}
		else if (menuItem.getItemId() == R.id.addMarker) {
			AddMarkerFragment addMarkerFragment = new AddMarkerFragment();
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_frame, addMarkerFragment).addToBackStack(null).commit();
		}
		return super.onOptionsItemSelected(menuItem);
	}

	public void loadAddNewMarkerFragment(LatLng location) {
		AddMarkerFragment addMarkerFragment = new AddMarkerFragment(location);
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, addMarkerFragment).addToBackStack(null).commit();
	}

	@Override
	public void onAddMarker(Point marker) {
		fragmentMap.addMarker(marker);
		fragmentManager.popBackStack();
	}
}
