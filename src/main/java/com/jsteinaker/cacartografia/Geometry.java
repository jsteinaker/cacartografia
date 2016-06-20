package com.jsteinaker.cacartografia;

import java.util.ArrayList;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Geometry {
	private String type;
	private ArrayList<Double> coordinates;

	public Geometry(LatLng location) {
		setType("Point");
		setCoordinates(location);
	}

	public String getType() {
		return type;
	}

	public ArrayList<Double> getCoordinates() {
		return coordinates;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public void setCoordinates(LatLng location) {
		coordinates = new ArrayList<Double>();
		coordinates.add(location.getLongitude());
		coordinates.add(location.getLatitude());
	}
}
