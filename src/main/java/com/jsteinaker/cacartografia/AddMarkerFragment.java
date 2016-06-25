package com.jsteinaker.cacartografia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class AddMarkerFragment extends Fragment {
	View fragmentView;
	LatLng position;
	OnFragmentInteractionListener interactionListener;
	TextView mTitleField;
	TextView mDescriptionField;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		interactionListener = (OnFragmentInteractionListener) getActivity();
		fragmentView = inflater.inflate(R.layout.add_marker, container, false);

		// Referencias a los campos de texto
		mTitleField = (TextView) fragmentView.findViewById(R.id.input_title);
		mDescriptionField = (TextView) fragmentView.findViewById(R.id.input_description);

		if (position != null)
		{
			TextView textView = (TextView) fragmentView.findViewById(R.id.input_location);
			textView.setText(position.getLatitude() + ";" + position.getLongitude());
		}

		// Modificaciones en la AppBar
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.addMarker);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		return fragmentView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.add_marker_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() == R.id.done)
		{
			// Terminar inmediatamente si faltan completar campos.
			if (!validateForm()) {
				return false;
			}
				
			Utils.hideKeyboard(getActivity());
			String title = mTitleField.getText().toString();
			String description = mDescriptionField.getText().toString();
			String id = "ID" + title;
			Geometry geometry = new Geometry(position);
			Properties properties = new Properties(id, title, description);
			Point marker = new Point(geometry, properties);
			interactionListener.onAddMarker(marker);
			return true;
		}
		return false;
	}
	
	private boolean validateForm() {
        boolean valid = true;

        String title = mTitleField.getText().toString();
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(getText(R.string.required));
            valid = false;
        } else {
            mTitleField.setError(null);
        }

        String description = mDescriptionField.getText().toString();
        if (TextUtils.isEmpty(description)) {
            mDescriptionField.setError(getText(R.string.required));
            valid = false;
        } else {
            mDescriptionField.setError(null);
        }

        return valid;
	}

	public void setLocation(LatLng location) {
		position = location;
	}
}
