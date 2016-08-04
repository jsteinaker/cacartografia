package com.jsteinaker.cacartografia;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class Database {
	
	private DatabaseReference database;

	public Database() {
		database = FirebaseDatabase.getInstance().getReference();
	}

	public void addMarker(Point marker, String id) {
		database.child("features").child(id).setValue(marker);
	}

	public void editMarker(Point marker, String id) {
		database.child("features").child(id).setValue(marker);
	}

	public void setUpListeners() {
		/* Ponemos listeners para responder a los cambios que puedan ocurrir en
		 * la base de datos (nuevo marcador, marcador suprimido, etc.) */
		database.child("features").addChildEventListener(new ChildEventListener() {
			/* Nuevo marcador */
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
				onMarkerAdded(dataSnapshot);
			}

			/* Marcador modificado */
			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
			}

			/* Marcador movido */
			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
			}

			/* Marcador eliminado */
			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {
				onMarkerDeleted(dataSnapshot);
			}
			
			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.w("DUALC", "ChildEventListener:onCancelled", databaseError.toException());
			}
		});
	}

	public void onMarkerAdded(DataSnapshot dataSnapshot) {
	}

	public void onMarkerDeleted(DataSnapshot dataSnapshot) {
	}
}
