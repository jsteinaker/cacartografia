package com.jsteinaker.cacartografia;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DUALC extends AppCompatActivity
{
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FragmentMap fragmentMap = new FragmentMap();
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_frame, fragmentMap).commit();

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() == R.id.about) {
			AboutFragment aboutFragment = new AboutFragment();
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_frame, aboutFragment).addToBackStack(null).commit();
		}
		return super.onOptionsItemSelected(menuItem);
	}
}
