package com.locationfinder.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import com.locationfinder.data.Location;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * 
 * This is simple singleton class which is supposed to provide buddy.com
 * services.
 * 
 * @author andreyd
 */
public class BuddyHelper {

	private static final String TAG = BuddyHelper.class.getName();

	private final Context context;
	private final String baseUrl;

	public BuddyHelper(Context context, String baseUrl) {
		this.context = context;
		this.baseUrl = baseUrl;
	}

	public boolean isInetAvailable() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public List<Location> getLocations(String latitude, String longitude, int limit) {
		if (!isInetAvailable()) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(getBaseUrl());
		HttpParams params = httpGet.getParams();
		params.setParameter("GeoLocation_Location_Search", "");
		params.setParameter("BuddyApplicationName", "Location Finder");
		params.setParameter("BuddyApplicationPassword", "46CFD459-93C7-43DC-8F5B-922AA4CD6B44");
		params.setParameter("UserToken", "**************");
		params.setIntParameter("SearchDistance", 100000); // Distance in METERS
		params.setDoubleParameter("Latitude", 47.675272d);
		params.setDoubleParameter("Longitude", 122.20624d);
		params.setIntParameter("RecordLimit", limit);
		params.setParameter("SearchName", "");
		params.setParameter("SearchCategoryID", "");
		httpGet.setParams(params);

		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				content.close();
			} else {
				Log.e(TAG, "Failed to download locations");
			}
		} catch (IOException e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		Log.d(TAG, "received text\n" + builder.toString());
		return null;
	}

}
