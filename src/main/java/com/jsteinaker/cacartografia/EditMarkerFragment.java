package com.jsteinaker.cacartografia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class EditMarkerFragment extends BaseEditMarkerFragment {

	private View newFragmentView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		newFragmentView = super.onCreateView(inflater, container, savedInstanceState);
		
		/* Rellenamos los campos, siempre y cuando tengamos los valores */
		if (position != null) {
			mLocationField.setText(position.getLatitude() + ";" + position.getLongitude());
		}
		if (title != null) {
			mTitleField.setText(title);
			if (description != null) {
				mDescriptionField.setText(description);
			}
		}

		/* Titulo de la AppBar */
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.edit_marker);

		return newFragmentView;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (super.onOptionsItemSelected(menuItem) == true) {
			interactionListener.onEditMarker(point, markerId);
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setParams(LatLng location, String title, String description, String id) {
		position = location;
		this.title = title;
		this.description = description;
		markerId = id;
	}
}
