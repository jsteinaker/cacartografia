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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.Exception;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.squareup.picasso.Picasso;

public class LoginFragment extends BaseFragment {
	
	/* Data*/
	private OnFragmentInteractionListener interactionListener;
	private FirebaseAuth auth;
	private FirebaseAuth.AuthStateListener authListener;
	private FirebaseUser user;
	private Database database;

	/* UI */
	private View fragmentView;
	private EditText mEmailField;
	private EditText mPasswordField;
	private ImageView avatar;
	private Button btn;
	private TextView userName;

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
		avatar = (ImageView) fragmentView.findViewById(R.id.avatar);
		userName = (TextView) fragmentView.findViewById(R.id.user_name);

		// Modificaciones en la AppBar
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if (user != null) {
			((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.user_account);
		}
		else {
			((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.login);
		}

		/* Listener para el botón de logueo/deslogueo */
		btn = (Button) fragmentView.findViewById(R.id.login_logout);
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

		/* Listener para el botón de registro */
		btn = (Button) fragmentView.findViewById(R.id.register);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((DUALC)getActivity()).loadRegisterFragment();
			}
		});
		
		/* Creamos un objeto database para las consultas */
		database = new Database() {
			/* Callback para actualizar el TextView cuando está lista la
			 * información desde la base de datos */
			@Override
			public void onUserFullNameReady() {
				userName.setText(fullName);
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

	private void logIn(String email, String password) {
		/* Terminar inmediatamente si alguno de los campos está vacío */
		if (!validateForm()) {
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
	}

	private void updateUI(FirebaseUser user) {
		hideProgressDialog();
		if (user != null) {
			/* Fuera campos de texto, cambia el texto del botón */
			fragmentView.findViewById(R.id.input_email_layout).setVisibility(View.GONE);
			fragmentView.findViewById(R.id.input_password_layout).setVisibility(View.GONE);
			((Button)fragmentView.findViewById(R.id.login_logout)).setText(R.string.logout);
			fragmentView.findViewById(R.id.dont_have_account).setVisibility(View.GONE);
			fragmentView.findViewById(R.id.register).setVisibility(View.GONE);
			/* Pide a la base de datos el nombre de usuario, cuando está listo,
			 * un callback actualiza el TextView */
			userName.setVisibility(View.VISIBLE);
			database.getUserFullName(user.getUid());
			/* Carga la imagen de Gravatar */
			avatar.setVisibility(View.VISIBLE);
			String hash = Utils.md5hash(user.getEmail());
			String gravatarURL = "http://www.gravatar.com/avatar/" + hash + "?s=256";
			Picasso.with(getActivity()).load(gravatarURL).into(avatar);
		}
		else {
			fragmentView.findViewById(R.id.input_email_layout).setVisibility(View.VISIBLE);
			fragmentView.findViewById(R.id.input_password_layout).setVisibility(View.VISIBLE);
			fragmentView.findViewById(R.id.dont_have_account).setVisibility(View.VISIBLE);
			fragmentView.findViewById(R.id.register).setVisibility(View.VISIBLE);
			((Button)fragmentView.findViewById(R.id.login_logout)).setText(R.string.login);
			userName.setVisibility(View.VISIBLE);
			avatar.setVisibility(View.GONE);
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

	/* Control de la tecla back */
	@Override
	public boolean onBackPressed() {
		return false;
	}

}
