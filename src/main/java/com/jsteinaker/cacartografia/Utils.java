package com.jsteinaker.cacartografia;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.view.View;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

	public static String md5hash(String input) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(input.getBytes());
			byte[] messageDigest = digest.digest();

			StringBuilder hexString = new StringBuilder();

			for (byte msg : messageDigest) {
				String h = Integer.toHexString(0xFF & msg);
				while (h.length() < 2) {
					h = "0" + h;
				}
				hexString.append(h);
			}

			return hexString.toString();
		
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return "";
	}

}
