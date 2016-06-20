package com.jsteinaker.cacartografia;

public class Properties {
	private String description;
	private String id;
	private String title;

	public Properties(String id, String title, String description) {
		setId(id);
		setTitle(title);
		setDescription(description);
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
