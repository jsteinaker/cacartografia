package com.jsteinaker.cacartografia;

import com.mapbox.mapboxsdk.annotations.Marker;

public class DUALCMarker extends Marker {

	private String owner;

	public DUALCMarker(DUALCMarkerOptions dualcMarkerOptions, String owner) {
		super(dualcMarkerOptions);
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

}
