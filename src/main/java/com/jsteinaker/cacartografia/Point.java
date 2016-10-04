package com.jsteinaker.cacartografia;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Point {
	private Geometry geometry;
	private Properties properties;
	private String type;
	private Long id;

	// Constructor
	public Point(Geometry geometry, Properties properties, Long id) {
		this.geometry = geometry;
		this.properties = properties;
		this.type = "Feature";
		this.id = id;
	}

	// Constructor sin argumentos, para Firebase
	public Point() {
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

	public Long getId() {
		return id;
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

	public void setId(Long id) {
		this.id = id;
	}

	/* Sobrecarga de toString()
	 * Tiene como objetivo poder usar este objeto en un ArrayAdapter convencional,
	 * sin necesidad de definir uno propio.
	 * El valor de retorno de esta función definirá el campo usado para buscar.
	 * Así, si retornamos el título, este será usado para la búsqueda */
	@Override
	public String toString() {
		return getProperties().getTitle();
	}
}
