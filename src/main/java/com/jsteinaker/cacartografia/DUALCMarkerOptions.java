package com.jsteinaker.cacartografia;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class DUALCMarkerOptions extends BaseMarkerOptions<DUALCMarker, DUALCMarkerOptions> implements Parcelable {

	private String owner;

	public DUALCMarkerOptions owner(String owner) {
		this.owner = owner;
		return getThis();
	}
	
	public DUALCMarkerOptions() {
	}
	
	private DUALCMarkerOptions(Parcel in) {
    	position((LatLng) in.readParcelable(LatLng.class.getClassLoader()));
        snippet(in.readString());
        String iconId = in.readString();
        Bitmap iconBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        Icon icon = IconFactory.recreate(iconId, iconBitmap);
        icon(icon);
		title(in.readString());
		owner(in.readString());
	}

	@Override
	public DUALCMarkerOptions getThis() {
		return this;
	}

	@Override
	public DUALCMarker getMarker() {
		return new DUALCMarker(this, owner);
	}

	public static final Parcelable.Creator<DUALCMarkerOptions> CREATOR
			= new Parcelable.Creator<DUALCMarkerOptions>() {
		public DUALCMarkerOptions createFromParcel(Parcel in) {
			return new DUALCMarkerOptions(in);
		}

		public DUALCMarkerOptions[] newArray(int size) {
			return new DUALCMarkerOptions[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(position, flags);
		out.writeString(snippet);
		out.writeString(icon.getId());
		out.writeParcelable(icon.getBitmap(), flags);
		out.writeString(title);
		out.writeString(owner);
	}
}
