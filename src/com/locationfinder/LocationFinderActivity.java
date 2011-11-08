package com.locationfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LocationFinderActivity extends Activity {

	private EditText latitude;
	private EditText longitude;
	private Button autoFill;
	private Button showLocations;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		latitude = (EditText) findViewById(R.id.latitude);
		longitude = (EditText) findViewById(R.id.longitude);
		autoFill = (Button) findViewById(R.id.autoFill);
		showLocations = (Button) findViewById(R.id.showLocations);

		showLocations.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LocationFinderActivity.this, LocationsActivity.class);
				intent.putExtra(LocationsActivity.LATITUDE, latitude.getText().toString());
				intent.putExtra(LocationsActivity.LONGITUDE, longitude.getText().toString());
				startActivity(intent);
			}
		});
	}
}