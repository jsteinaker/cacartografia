package com.jsteinaker.cacartografia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterFragment extends BaseFragment {
	/* UI */
	private View fragmentView;
	private Button btn;
	private EditText mNameField;
	private EditText mEmailField;
	private EditText mPasswordField;
	private EditText mRepeatPasswordField;

	/* Data */
	private Database database;
	private OnFragmentInteractionListener interactionListener;
	private FirebaseAuth auth;
	private String name;
	private String email;
	private String password;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* Objetos para consultas */
		auth = FirebaseAuth.getInstance();
		database = new Database();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (fragmentView == null) {
			fragmentView = inflater.inflate(R.layout.register, container, false);
		}

		interactionListener = (OnFragmentInteractionListener) getActivity();

		// Modificaciones en la AppBar
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.register);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		/* Referencias a los controles UI */
		mNameField = (EditText) fragmentView.findViewById(R.id.input_name);
		mEmailField = (EditText) fragmentView.findViewById(R.id.input_email);
		mPasswordField = (EditText) fragmentView.findViewById(R.id.input_password);
		mRepeatPasswordField = (EditText) fragmentView.findViewById(R.id.repeat_password);

		/* Listener para el botón de registro */
		btn = (Button) fragmentView.findViewById(R.id.register);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!validateForm()) {
					return;
				}

				/* Mostrar diálogo de progreso para bloquear la interfaz
				 * mientras se hace el registro online. */
				showProgressDialog();
				auth.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete(@NonNull Task<AuthResult> task) {
							hideProgressDialog();
							if (!task.isSuccessful()) {
								Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
							}
							else {
								database.setUserFullName(database.getUser().getUid(), name);
								interactionListener.onRegister();
							}
						}
					});
			}
		});

		return fragmentView;
	}

	/* Valida los datos del formulario */
	private boolean validateForm() {
		boolean valid = true;

		name = mNameField.getText().toString();
		if (TextUtils.isEmpty(name)) {
			mNameField.setError(getText(R.string.required));
			valid = false;
		}

		email = mEmailField.getText().toString();
		if (TextUtils.isEmpty(email)) {
			mEmailField.setError(getText(R.string.required));
			valid = false;
		}

		password = mPasswordField.getText().toString();
		if (TextUtils.isEmpty(password)) {
			mPasswordField.setError(getText(R.string.required));
			valid = false;
		}

		String repeatPassword = mRepeatPasswordField.getText().toString();
		if (!password.equals(repeatPassword)) {
			mPasswordField.setError(getText(R.string.passwords_dont_match));
			valid = false;
		}

		return valid;
	}
	
	/* Control de la tecla back */
	@Override
	public boolean onBackPressed() {
		return false;
	}
}
