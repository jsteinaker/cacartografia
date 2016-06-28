package com.jsteinaker.cacartografia;

import java.util.ArrayList;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import com.mapbox.mapboxsdk.geometry.LatLng;

@IgnoreExtraProperties
public class Geometry {
	private String type;
	private ArrayList<Double> coordinates;

	/* Constructor sin argumentos, para Firebase */
	public Geometry() {
		setType("Point");
	}

	public String getType() {
		return type;
	}

	public ArrayList<Double> getCoordinates() {
		return coordinates;
	}

	@Exclude
	public LatLng getCoordinatesInLatLng() {
		LatLng latLng = new LatLng(coordinates.get(1), coordinates.get(0));
		return latLng;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCoordinates(ArrayList<Double> coordinates) {
		this.coordinates = coordinates;
	}

	@Exclude
	public void setCoordinatesFromLatLng(LatLng location) {
		coordinates = new ArrayList<Double>();
		coordinates.add(location.getLongitude());
		coordinates.add(location.getLatitude());
	}
}
