package com.jsteinaker.cacartografia;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Database {
	
	private DatabaseReference database;
	protected String fullName;

	public Database() {
		database = FirebaseDatabase.getInstance().getReference();
	}

	public void addMarker(Point marker) {
		database.child("features").push().setValue(marker);
	}

	public void editMarker(Point marker, String id) {
		database.child("features").child(id).setValue(marker);
	}

	public void deleteMarker(String id) {
		database.child("features").child(id).removeValue();
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

		database.child("features").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				onMarkerDataChanged(dataSnapshot);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.w("DUALC", "ValueEventListener:onCancelled", databaseError.toException());
			}
		});

	}

	public void onMarkerAdded(DataSnapshot dataSnapshot) {
	}

	public void onMarkerDeleted(DataSnapshot dataSnapshot) {
	}

	public void onMarkerDataChanged(DataSnapshot dataSnapshot) {
	}

	public FirebaseUser getUser() {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		return auth.getCurrentUser();
	}
	
	public void getUserFullName(String uid) {
		database.child("user_data").child(uid).child("full_name").addListenerForSingleValueEvent(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						fullName = dataSnapshot.getValue(String.class);
						onUserFullNameReady();
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						Log.w("DUALC", databaseError.toException());
					}
				});
	
	}

	public void onUserFullNameReady() {
	}

	public void setUserFullName(String uid, String fullName) {
		database.child("user_data").child(uid).child("full_name").setValue(fullName);
	}
}
