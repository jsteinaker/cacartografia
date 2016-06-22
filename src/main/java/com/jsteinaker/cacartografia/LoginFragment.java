package com.jsteinaker.cacartografia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
	View fragmentView;
	OnFragmentInteractionListener interactionListener;
	private FirebaseAuth auth;
	private FirebaseAuth.AuthStateListener authListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		interactionListener = (OnFragmentInteractionListener) getActivity();
		fragmentView = inflater.inflate(R.layout.login, container, false);

		// Modificaciones en la AppBar
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.login);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		auth = FirebaseAuth.getInstance();
		authListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					Log.d("DUALC", "Logueado");
				}
				else
				{
					Log.d("DUALC", "Sin loguear");
				}
			}
		};
		
		return fragmentView;
	}

	@Override
	public void onStart() {
		super.onStart();
		/* Registrar el listener a la base de datos cuando arranca el fragmento */
		auth.addAuthStateListener(authListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		/* Y desregistrarlo cuando termina el fragmento */
		auth.removeAuthStateListener(authListener);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.login_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() == R.id.done)
		{
			Utils.hideKeyboard(getActivity());
			TextView textView = (TextView) fragmentView.findViewById(R.id.input_email);
			String email = textView.getText().toString();
			textView = (TextView) fragmentView.findViewById(R.id.input_password);
			String password = textView.getText().toString();
			logIn(email, password);
			return true;
		}
		return false;
	}

	private void logIn(String email, String password) {
		auth.signInWithEmailAndPassword(email, password).
			addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(Task<AuthResult> task) {
					if (!task.isSuccessful()) {
						Toast.makeText(getActivity(), R.string.auth_failed, Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getActivity(), R.string.auth_ok, Toast.LENGTH_SHORT).show();
					}
				}
			});
	}

	private void logOut() {
		auth.signOut();
		Log.w("DUALC", "Deslogueado");
	}

}
