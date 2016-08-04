package com.jsteinaker.cacartografia;

import com.mapbox.mapboxsdk.annotations.Marker;

public class DUALCMarker extends Marker {

	private String owner;
	private Long dualcId;

	public DUALCMarker(DUALCMarkerOptions dualcMarkerOptions, String owner, Long dualcId) {
		super(dualcMarkerOptions);
		this.owner = owner;
		this.dualcId = dualcId;
	}

	public String getOwner() {
		return owner;
	}

	public Long getDualcId() {
		return dualcId;
	}

}
