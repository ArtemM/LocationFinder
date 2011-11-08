package com.locationfinder.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.locationfinder.data.Location;

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
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		
		params.add(new BasicNameValuePair("BuddyApplicationName", "LocationFInder"));
		params.add(new BasicNameValuePair("BuddyApplicationPassword", "738EF0E0-82A0-48EE-B5A6-022F163E574B"));
		params.add(new BasicNameValuePair("UserToken", "UT-2bac14d1-4176-4f8a-be70-51104653f8a4"));
		params.add(new BasicNameValuePair("SearchDistance", "1000000")); // Distance in METERS
		params.add(new BasicNameValuePair("Latitude", latitude));
		params.add(new BasicNameValuePair("Longitude", longitude));
		params.add(new BasicNameValuePair("RecordLimit", String.valueOf(limit)));
		params.add(new BasicNameValuePair("SearchName", ""));
		params.add(new BasicNameValuePair("SearchCategoryID", ""));
		
		String paramString = URLEncodedUtils.format(params, "utf-8");
		
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(getBaseUrl() + "?GeoLocation_Location_Search&" + paramString);
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
		return parseLocations(builder.toString());
	}

	/**
	 * 
	 * @param json - locations data in form of json
	 * @return
	 */
	private List<Location> parseLocations(String json) {
		List<Location> result = new ArrayList<Location>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); ++i) {
				JSONObject object = jsonArray.getJSONObject(i);
				Location location = new Location();
				location.setName(object.getString("name"));
				location.setCity(object.getString("city"));
				result.add(location);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
}
