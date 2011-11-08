package com.locationfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LocationFinderActivity extends Activity {

	private EditText latitude;
	private EditText longitude;
	private Button autoFill;
	private Button showLocations;

	private LocationManager lm;
	private Location location;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		latitude = (EditText) findViewById(R.id.latitude);
		longitude = (EditText) findViewById(R.id.longitude);
		autoFill = (Button) findViewById(R.id.autoFill);
		showLocations = (Button) findViewById(R.id.showLocations);

		autoFill.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (location != null) {
					latitude.setText(String.valueOf(location.getLatitude()));
					longitude.setText(String.valueOf(location.getLongitude()));
				} else {
					Toast.makeText(LocationFinderActivity.this, "Unable to get location pls try later",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
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

	@Override
	protected void onResume() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
		}

		super.onResume();
	}
	
	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			LocationFinderActivity.this.location = location;
			lm.removeUpdates(this);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}
	};
}