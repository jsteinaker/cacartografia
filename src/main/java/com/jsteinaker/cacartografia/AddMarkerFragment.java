package com.jsteinaker.cacartografia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

	public AddMarkerFragment(LatLng location) {
		position = location;
	}

	public AddMarkerFragment() {
		position = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		interactionListener = (OnFragmentInteractionListener) getActivity();
		fragmentView = inflater.inflate(R.layout.add_marker, container, false);

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
			Utils.hideKeyboard(getActivity());
			TextView textView = (TextView) fragmentView.findViewById(R.id.input_title);
			String title = textView.getText().toString();
			textView = (TextView) fragmentView.findViewById(R.id.input_description);
			String description = textView.getText().toString();
			String id = "ID" + title;
			Geometry geometry = new Geometry(position);
			Properties properties = new Properties(id, title, description);
			Point marker = new Point(geometry, properties);
			interactionListener.onAddMarker(marker);
			return true;
		}
		return false;
	}
}
