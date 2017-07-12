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
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.Mapbox;

public class DUALC extends AppCompatActivity implements OnFragmentInteractionListener, BackHandlerInterface
{
	private FragmentManager fragmentManager;
	private FragmentMap fragmentMap;
	private LoginFragment loginFragment;
	private BaseFragment selectedFragment;
	
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		// Mapbox initialization
		Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

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
			Utils.hideKeyboard(this);
			getSupportFragmentManager().popBackStack();
		}
		else if (menuItem.getItemId() == R.id.about) {
			AboutFragment aboutFragment = new AboutFragment();
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_frame, aboutFragment).addToBackStack(null).commit();
		}
		else if (menuItem.getItemId() == R.id.help) {
			HelpFragment helpFragment = new HelpFragment();
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_frame, helpFragment).addToBackStack(null).commit();
		}
		else if (menuItem.getItemId() == R.id.login) {
			if (loginFragment == null) {
				loginFragment = new LoginFragment();
			}
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_frame, loginFragment).addToBackStack(null).commit();
		}
		return super.onOptionsItemSelected(menuItem);
	}

	public void loadAddNewMarkerFragment(LatLng location) {
		AddMarkerFragment addMarkerFragment = new AddMarkerFragment();
		addMarkerFragment.setParams(location);
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, addMarkerFragment).addToBackStack(null).commit();
	}

	public void loadEditMarkerFragment(DUALCMarker marker, String markerId) {
		EditMarkerFragment editMarkerFragment = new EditMarkerFragment();
		editMarkerFragment.setParams(marker.getPosition(),
				marker.getTitle(), marker.getSnippet(), markerId);
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, editMarkerFragment)
			.addToBackStack(null)
			.commit(); 
	}

	public void loadRegisterFragment() {
		RegisterFragment registerFragment = new RegisterFragment();
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_frame, registerFragment)
			.addToBackStack(null)
			.commit();
	}

	@Override
	public void onAddMarker() {
		fragmentManager.popBackStack();
	}

	@Override
	public void onEditMarker() {
		fragmentManager.popBackStack();
	}

	@Override
	public void onDeleteMarker() {
		fragmentMap.deselectMarkers();
		fragmentManager.popBackStack();
	}

	@Override
	public void onLoginCorrect() {
		fragmentManager.popBackStack();
	}

	@Override
	public void onRegister() {
		fragmentManager.popBackStack();
	}

	/* Control de la tecla back */
	@Override
	public void onBackPressed() {
		if (selectedFragment == null || !selectedFragment.onBackPressed()) {
			super.onBackPressed();
		}
	}

	@Override
	public void setSelectedFragment(BaseFragment fragment) {
		selectedFragment = fragment;
	}
}
