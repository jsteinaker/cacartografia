package com.jsteinaker.cacartografia;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Point {
	private String title;
	private String snippet;
	private LatLng location;

	// Constructor
	public Point(String title, String snippet, LatLng location) {
		this.title = title;
		this.snippet = snippet;
		this.location = location;
	}

	// Getters
	public String getTitle() {
		return title;
	}
	public String getSnippet() {
		return snippet;
	}
	public LatLng getLocation() {
		return location;
	}

	// Setters
	public void setTitle(String title) {
		this.title = title;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	public void setLocation(LatLng location) {
		this.location = location;
	}
}
