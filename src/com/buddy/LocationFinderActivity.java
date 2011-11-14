package com.buddy;

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

/**
 * This Activity handles initial application screen. Handles automatic location
 * recognition at application startup. This location might be used later to be
 * used with "Auto-Fill Location" Starts LocationsActivity and pass latitude and
 * longitude there.
 */
public class LocationFinderActivity extends Activity {

	private EditText latitude;
	private EditText longitude;
	private Button autoFill;
	private Button showLocations;

	private LocationManager locationManager;
	private Location location;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		latitude = (EditText) findViewById(R.id.latitude);
		longitude = (EditText) findViewById(R.id.longitude);
		autoFill = (Button) findViewById(R.id.autoFill);
		showLocations = (Button) findViewById(R.id.showLocations);

		// Adding handler for "Auto-Fill Location" button
		autoFill.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// If location is successfully set by calculateLocation function
				// we will took location value to fill location EditTexts
				if (location != null) {
					latitude.setText(String.valueOf(location.getLatitude()));
					longitude.setText(String.valueOf(location.getLongitude()));
				} else {
					// Otherwise we showing Toast to notify user that we still did not get any location information.
					// In this case user may try to press "Auto-Fill Location" button later. 
					// It is possible that GPS coordinates will be already taken  at that moment.
					Toast.makeText(LocationFinderActivity.this, "Unable to get location, please try later",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		showLocations.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Preparing Intent to switch to LocationsActivity
				Intent intent = new Intent(LocationFinderActivity.this, LocationsActivity.class);
				// Adding data for LocationsActivity
				intent.putExtra(LocationsActivity.LATITUDE, latitude.getText().toString());
				intent.putExtra(LocationsActivity.LONGITUDE, longitude.getText().toString());

				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		calculateLocation();
		super.onResume();
	}

	/**
	 * Calculates current location using {@code locationManager} and set it to
	 * the internal class field {@code location}. First it tries to get last
	 * known location which is stored on device since last time GPS was turned
	 * on. If this fails then it requests update of location information.
	 */
	private void calculateLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			int gpsChangeUpdateFrequency = 2000; // Frequency of calling locationListener in milliseconds
			int minimalDistance = 10; // The minimum distance interval for notifications, in meters
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsChangeUpdateFrequency,
					minimalDistance, locationListener);
		}
	}

	/**
	 * This is listener which will be activated when GPS will get updates on
	 * current location. Used in {@code calculateLocation} method
	 */
	final LocationListener locationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			LocationFinderActivity.this.location = location;
			locationManager.removeUpdates(this);
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