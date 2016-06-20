package com.jsteinaker.cacartografia;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.view.View;

public class Utils {
	
	public static void hideKeyboard(Context ctx) {
    InputMethodManager inputManager = (InputMethodManager) ctx
    .getSystemService(Context.INPUT_METHOD_SERVICE);

    // check if no view has focus:
     View v = ((AppCompatActivity) ctx).getCurrentFocus();
     if (v == null)
        return;

    inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
 	
	}
}
