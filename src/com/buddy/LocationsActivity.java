package com.buddy;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.buddy.data.Location;
import com.buddy.tools.BuddyHelper;

/**
 * Activity which will be started when user will press "Show Locations" button.
 * It set get user's location from <tt>Intent</tt> and uses BuddyHelper to
 * retrieve list of locations from server. Then adds list of locations to the
 * <tt>ListView</tt>
 */
public class LocationsActivity extends ListActivity {

	private static final int LOCATIONS_RESULT_LIMIT = 7;
	private static final String API_ENDPOINT_URL = "http://webservice.buddyplatform.com/Service/v1/BuddyService.ashx";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";

	private BuddyHelper buddyHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.locations);
		
		//Creating class which will make actual requests to the server
		buddyHelper = new BuddyHelper(this, API_ENDPOINT_URL);
		
		//Getting user's location from Intent
		String latitude = getIntent().getStringExtra(LATITUDE);
		String longitude = getIntent().getStringExtra(LONGITUDE);
		
		//Get list of locations already parsed and wrapped into the bean object 
		List<Location> locations = buddyHelper.getLocations(latitude, longitude, LOCATIONS_RESULT_LIMIT);

		//If nothing is found then setting view to say "Noting found" 
		getListView().setEmptyView(findViewById(R.id.emptyList));
		
		//Showing actually found locations.  
		setListAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, locations));
	}

}
