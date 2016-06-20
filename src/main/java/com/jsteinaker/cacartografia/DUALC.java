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

public class DUALC extends AppCompatActivity
{
	FragmentManager fragmentManager;
	
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		fragmentManager = getSupportFragmentManager();
			
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
				Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_frame);
				if (fragment instanceof AboutFragment)
				{
					getSupportActionBar().setDisplayHomeAsUpEnabled(true);
					getSupportActionBar().setTitle(R.string.about);
				}
				else if (fragment instanceof FragmentMap)
				{
					getSupportActionBar().setDisplayHomeAsUpEnabled(false);
					getSupportActionBar().setTitle(R.string.app_name);
				}
				else if (fragment instanceof AddMarkerFragment)
				{
					getSupportActionBar().setDisplayHomeAsUpEnabled(true);
					getSupportActionBar().setTitle(R.string.addMarker);
				}
			}
        });

		FragmentMap fragmentMap = new FragmentMap();
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_frame, fragmentMap).commit();

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
}
