package com.jsteinaker.cacartografia;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

public abstract class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog;
	protected AlertDialog.Builder alertDialog;
	protected BackHandlerInterface backHandlerInterface;
	
	public abstract boolean onBackPressed();

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.logging));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

	public void createDialog(String message) {
		alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setMessage(message);
		alertDialog.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alertDialog.setNegativeButton(getString(R.string.cancel), new  DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	public void showDialog() {
		alertDialog.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		backHandlerInterface = (BackHandlerInterface) getActivity();
	}

	@Override
	public void onStart() {
		super.onStart();
		backHandlerInterface.setSelectedFragment(this);
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

}
