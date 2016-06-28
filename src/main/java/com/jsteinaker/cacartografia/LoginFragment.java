package com.jsteinaker.cacartografia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.Exception;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends BaseFragment {
	
	/* Data*/
	OnFragmentInteractionListener interactionListener;
	private FirebaseAuth auth;
	private FirebaseAuth.AuthStateListener authListener;
	private FirebaseUser user;

	/* UI */
	private View fragmentView;
	private EditText mEmailField;
	private EditText mPasswordField;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		auth = FirebaseAuth.getInstance();

		authListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				user = firebaseAuth.getCurrentUser();
				
				/* Actualizamos la interfaz cuando haya logueo o deslogueo */
				updateUI(user);
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		interactionListener = (OnFragmentInteractionListener) getActivity();
		fragmentView = inflater.inflate(R.layout.login, container, false);
		
		// Obtenemos referencia a los controles UI apenas inflado el layout
		mEmailField = (EditText) fragmentView.findViewById(R.id.input_email);
		mPasswordField = (EditText) fragmentView.findViewById(R.id.input_password);

		// Modificaciones en la AppBar
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.login);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		/* Listener para el botón de logueo/deslogueo */
		Button btn = (Button) fragmentView.findViewById(R.id.login_logout);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (user != null) {
					logOut();
				}
				else {
					Utils.hideKeyboard(getActivity());
					logIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
				}
			}
		});
		
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
	
	/* SIN MENU DE MOMENTO
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
			logIn(mEmailField.getText().toString(), mPasswordField.getText.toString());
			return true;
		}
		return false;
	}
	*/

	private void logIn(String email, String password) {
		/* Terminar inmediatamente si alguno de los campos está vacío */
		if (!validateForm())
		{
			return;
		}
		
		/*Mostrar diálogo de progreso para bloquear la interfaz mientras se hace el logueo */
		showProgressDialog();

		auth.signInWithEmailAndPassword(email, password).
			addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					/*Ocultar el diálogo de progreso y liberar la interfaz */
					hideProgressDialog();
					if (user != null) {
						interactionListener.onLoginCorrect();
					}
				}
			})
			.addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					if (e instanceof FirebaseNetworkException) {
						Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
					}
				}
			});
	}

	private void logOut() {
		/* No se usa diálogo de progreso porque debería ser instantáneo */
		auth.signOut();
		updateUI(null);
		Log.w("DUALC", "Deslogueado");
	}

	private void updateUI(FirebaseUser user) {
		hideProgressDialog();
		if (user != null) {
			fragmentView.findViewById(R.id.input_email_layout).setVisibility(View.GONE);
			fragmentView.findViewById(R.id.input_password_layout).setVisibility(View.GONE);
			((Button)fragmentView.findViewById(R.id.login_logout)).setText(R.string.logout);
		}
		else {
			fragmentView.findViewById(R.id.input_email_layout).setVisibility(View.VISIBLE);
			fragmentView.findViewById(R.id.input_password_layout).setVisibility(View.VISIBLE);
			((Button)fragmentView.findViewById(R.id.login_logout)).setText(R.string.login);
		}
	}

	private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getText(R.string.required));
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getText(R.string.required));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
	}

}
