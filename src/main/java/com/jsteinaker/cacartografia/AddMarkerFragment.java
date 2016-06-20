package com.jsteinaker.cacartografia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class AddMarkerFragment extends Fragment {
	View fragmentView;
	LatLng position;

	public AddMarkerFragment(LatLng location) {
		position = location;
	}

	public AddMarkerFragment()
	{
		position = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		fragmentView = inflater.inflate(R.layout.add_marker, container, false);

		if (position != null)
		{
			TextView textView = (TextView) fragmentView.findViewById(R.id.input_location);
			textView.setText(position.getLatitude() + ";" + position.getLongitude());
		}

		return fragmentView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.add_marker_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
}
