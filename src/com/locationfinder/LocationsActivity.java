package com.locationfinder;

import java.util.List;

import com.locationfinder.data.Location;
import com.locationfinder.tools.BuddyHelper;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class LocationsActivity extends ListActivity {

	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";

	private BuddyHelper buddyHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.locations);

		buddyHelper = new BuddyHelper(this, "http://webservice.buddyplatform.com/Service/v1/BuddyService.ashx");

		String latitude = getIntent().getStringExtra(LATITUDE);
		String longitude = getIntent().getStringExtra(LONGITUDE);

		List<Location> locations = buddyHelper.getLocations(latitude, longitude, 9);

		setListAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, locations));
	}

}
