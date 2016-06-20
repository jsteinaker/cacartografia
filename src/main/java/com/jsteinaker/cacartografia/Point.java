package com.jsteinaker.cacartografia;

public class Point {
	private Geometry geometry;
	private Properties properties;
	private String type;

	// Constructor
	public Point(Geometry geometry, Properties properties) {
		this.geometry = geometry;
		this.properties = properties;
		this.type = "Feature";
	}

	// Getters
	public Geometry getGeometry() {
		return geometry;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getType() {
		return type;
	}

	// Setters
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setType() {
		this.type = "Feature";
	}
}
