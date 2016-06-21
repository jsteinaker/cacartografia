package com.jsteinaker.cacartografia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {
	View fragmentView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (fragmentView == null) {
			fragmentView = inflater.inflate(R.layout.about, container, false);
		}

		// Modificaciones en la AppBar
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.about);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		return fragmentView;
	}
}
