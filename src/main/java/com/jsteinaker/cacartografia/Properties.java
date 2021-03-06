package com.jsteinaker.cacartografia;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Properties {
	private String description;
	private String title;
	private String owner;

	public Properties(String title, String description, String owner) {
		setTitle(title);
		setDescription(description);
		setOwner(owner);
	}

	/* Constructor sin argumentos, para Firebase */
	public Properties() {
	}

	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}

	public String getOwner() {
		return owner;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
