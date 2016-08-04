package com.jsteinaker.cacartografia;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class EditMarkerFragment extends BaseEditMarkerFragment {

	private View newFragmentView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		newFragmentView = super.onCreateView(inflater, container, savedInstanceState);
		
		/* Rellenamos los campos, siempre y cuando tengamos los valores */
		if (position != null) {
			mLocationField.setText(position.getLatitude() + ";" + position.getLongitude());
		}
		if (title != null) {
			mTitleField.setText(title);
			if (description != null) {
				mDescriptionField.setText(description);
			}
		}
		
		/* Botón de eliminar marcador visible, porque estamos editando */
		Button deleteMarker = (Button) fragmentView.findViewById(R.id.delete_marker);
		deleteMarker.setVisibility(View.VISIBLE);
		/* Y el listener */
		deleteMarker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createDialog(getString(R.string.confirm_delete_marker));
				showDialog();
			}
		});

		/* Titulo de la AppBar */
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.edit_marker);

		return newFragmentView;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (super.onOptionsItemSelected(menuItem) == true) {
			database.editMarker(point, markerId.toString());
			interactionListener.onEditMarker();
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void createDialog(String message) {
		super.createDialog(message);
		alertDialog.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				database.deleteMarker(markerId.toString());
				dialog.dismiss();
				interactionListener.onDeleteMarker();
			}
		});

	}
	
	public void setParams(LatLng location, String title, String description, Long id) {
		position = location;
		this.title = title;
		this.description = description;
		markerId = id;
	}

}
