package com.jsteinaker.cacartografia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class AddMarkerFragment extends BaseEditMarkerFragment {
	
	private View newFragmentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		newFragmentView = super.onCreateView(inflater, container, savedInstanceState);
		
		/* Rellenamos los campos, siempre y cuando tengamos los valores */
		if (position != null) {
			mLocationField.setText(position.getLatitude() + ";" + position.getLongitude());
		}

		/* Titulo de la AppBar */
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.addMarker);

		return newFragmentView;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (super.onOptionsItemSelected(menuItem) == true) {
			database.addMarker(point, markerId);
			interactionListener.onAddMarker();
			return true;
		}
		else {
			return false;
		}
	}

	public void setParams(LatLng location, String id) {
		position = location;
		markerId = id;
	}
}
