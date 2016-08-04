package com.jsteinaker.cacartografia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseEditMarkerFragment extends BaseFragment {
	/* Data */
	protected View fragmentView;
	protected LatLng position;
	protected String title;
	protected String description;
	protected Point point;
	protected Long markerId;
	protected Database database;
	
	/* UI */
	protected OnFragmentInteractionListener interactionListener;
	protected TextView mTitleField;
	protected TextView mDescriptionField;
	protected TextView mLocationField;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		interactionListener = (OnFragmentInteractionListener) getActivity();
		database = new Database();
		fragmentView = inflater.inflate(R.layout.edit_marker, container, false);

		// Referencias a los campos de texto
		mTitleField = (TextView) fragmentView.findViewById(R.id.input_title);
		mDescriptionField = (TextView) fragmentView.findViewById(R.id.input_description);
		mLocationField = (TextView) fragmentView.findViewById(R.id.input_location);

		/* Rellenamos los campos, siempre y cuando tengamos los valores */
		if (position != null) {
			mLocationField.setText(position.getLatitude() + ";" + position.getLongitude());
		}

		// En cualquiera de los casos, queremos el botón de Back
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		return fragmentView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.edit_marker_menu, menu);
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
			title = mTitleField.getText().toString();
			description = mDescriptionField.getText().toString();
			FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
			if (user == null) {
				return false;
			}
			String owner = user.getEmail();
			Geometry geometry = new Geometry();
			geometry.setCoordinatesFromLatLng(position);
			Properties properties = new Properties(title, description, owner);
			point = new Point(geometry, properties, markerId);
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

}
