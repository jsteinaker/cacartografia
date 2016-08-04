package com.jsteinaker.cacartografia;

import android.util.Log;

import java.lang.Exception;
import java.util.List;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.commons.utils.PolylineUtils;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;
import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Route {
	
	private static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoianN0ZWluYWtlciIsImEiOiI4Zjc4YTFiNzkwMWFiYmFhZTVhNjJjODdkZGM5YzM1NiJ9.opMzYPAFV5uhK3f_UIqKcQ";

	public static final String WALKING = DirectionsCriteria.PROFILE_WALKING;
	public static final String CYCLING = DirectionsCriteria.PROFILE_CYCLING;
	public static final String DRIVING = DirectionsCriteria.PROFILE_DRIVING;

	private Position mOrigin;
	private Position mDestination;
	private DirectionsRoute route;
	protected LatLng[] waypoints;

	public Route(LatLng origin, LatLng destination) {
		mOrigin = Position.fromCoordinates(origin.getLongitude(), origin.getLatitude());
		mDestination = Position.fromCoordinates(destination.getLongitude(), destination.getLatitude());
	}

	public void calculate(String profile) {
		try {
			MapboxDirections client = new MapboxDirections.Builder()
				.setAccessToken(MAPBOX_ACCESS_TOKEN)
				.setOrigin(mOrigin)
				.setDestination(mDestination)
				.setProfile(profile)
				.build();
			client.enqueueCall(new Callback<DirectionsResponse>() {
				@Override
				public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
					if (response.body() == null) {
						return;
					}
					route = response.body().getRoutes().get(0);
					List<Position> positions = PolylineUtils.decode(route.getGeometry(), 5);
					waypoints = new LatLng[positions.size()];
					int i = 0;
					for (Position point : positions) {
						waypoints[i] = new LatLng(point.getLatitude(), point.getLongitude());
						i++;
					}
					onRouteReady();
				}

				@Override
				public void onFailure(Call<DirectionsResponse> call, Throwable e) {
					Log.w("DUALC", "Error");
				}
			});
		}
		catch (Exception e) {
			Log.w("DUALC", e.toString());
		}
	}

	public double getDistance() {
		return route.getDistance();
	}

	public LatLng[] getWaypoints() {
		return waypoints;
	}

	public void onRouteReady() {
	}

}
