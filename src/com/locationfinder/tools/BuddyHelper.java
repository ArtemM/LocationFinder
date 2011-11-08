package com.locationfinder.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
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
 * Class which provides access to buddy.com services.
 * 
 */
public class BuddyHelper {

	private static final String TAG = BuddyHelper.class.getName();

	private final Context context;
	private final String endpointUrl;

	/**
	 * Initializes newly created object and set Android <tt>Context</tt> and URL
	 * to access to the server API.
	 * 
	 * @param context
	 *            locations Activity
	 * @param endpointUrl
	 *            <tt>String</tt> pointing to the API on server
	 */
	public BuddyHelper(Context context, String endpointUrl) {
		this.context = context;
		this.endpointUrl = endpointUrl;
	}

	/**
	 * Determines if connection to the Internet available
	 * 
	 * @return {@code true} if Internet connection available {@code false}
	 *         otherwise
	 */
	public boolean isInternetConnectionAvailable() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public String getEndpointUrl() {
		return endpointUrl;
	}

	/**
	 * Request server for locations in JSON format using location information
	 * provided by user. Prepares list of {@code Location} beans to be later
	 * shown in the list.
	 * 
	 * @param latitude
	 *            user's latitude taken from GPS
	 * @param longitude
	 *            user's longitude taken from GPS
	 * @param limit
	 *            results list will be limited to such number
	 * @return list of locations found
	 */
	public List<Location> getLocations(String latitude, String longitude, int limit) {
		List<Location> result = new ArrayList<Location>(limit);
		// If no Internet connection available then we will not be able to query server so returning empty list
		if (!isInternetConnectionAvailable()) {
			return result;
		}

		// Preparing parameters according to  http://m.buddy.com/APIDocumentation/DocHome.aspx?Topic=GeoLocation_Location_CustomSearch
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("BuddyApplicationName", "LocationFInder"));
		params.add(new BasicNameValuePair("BuddyApplicationPassword", "738EF0E0-82A0-48EE-B5A6-022F163E574B"));
		params.add(new BasicNameValuePair("UserToken", "UT-2bac14d1-4176-4f8a-be70-51104653f8a4"));
		params.add(new BasicNameValuePair("SearchDistance", "1000000")); // Distance
																			// in
																			// METERS
		params.add(new BasicNameValuePair("Latitude", latitude));
		params.add(new BasicNameValuePair("Longitude", longitude));
		params.add(new BasicNameValuePair("RecordLimit", String.valueOf(limit)));
		params.add(new BasicNameValuePair("SearchName", ""));
		params.add(new BasicNameValuePair("SearchCategoryID", ""));

		String paramString = URLEncodedUtils.format(params, "utf-8");

		HttpClient client = new DefaultHttpClient();
		// Can't pass empty parameters without "=" so adding this directly to
		// the request string
		HttpGet httpGet = new HttpGet(getEndpointUrl() + "?GeoLocation_Location_Search&" + paramString);

		HttpResponse response = null;
		try {
			response = client.execute(httpGet);
		} catch (ClientProtocolException e) {
			Log.e(TAG, "Can't access to the server " + e.toString());
			return result;
		} catch (IOException e) {
			Log.e(TAG, "IO exception during connection to server " + e.toString());
			return result;
		}

		StatusLine statusLine = response.getStatusLine();
		// Get status code for response
		int statusCode = statusLine.getStatusCode();
		if (statusCode == HttpURLConnection.HTTP_OK) {
			String json = readResponseString(response);
			result = parseLocations(json);
		} else {
			Log.e(TAG, "Failed to download locations");
			return result;
		}
		return result;
	}

	/**
	 * Reads response from the server <tt>response</tt> object and returns it as
	 * <tt>String</tt>
	 * 
	 * @param response
	 *            server API response
	 * @return string in JSON format
	 */
	private String readResponseString(HttpResponse response) {
		StringBuilder builder = new StringBuilder();
		InputStream content = null;
		try {
			// Get data stream containing response from server
			content = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (IllegalStateException e) {
			Log.e(TAG, "Can't read data from server. Response state is not valid " + e.toString());
			return "";
		} catch (IOException e) {
			Log.e(TAG, "Can't read data from server. IO error during read " + e.toString());
			return "";
		} finally {
			if (content != null)
				try {
					content.close();
				} catch (IOException e) {
					// Suppress error
				}
		}
		return builder.toString();
	}

	/**
	 * Retrieve locations data from result returned from server
	 * 
	 * @param json
	 *            locations data in JSON format
	 * @return list of location beans
	 */
	private List<Location> parseLocations(String json) {
		List<Location> result = new ArrayList<Location>();
		// If no JSON provided we will return empty list
		if (null == json || "".equals(json.trim()))
			return result;

		try {
			JSONObject jsonObject = new JSONObject(json);
			// Get data array from response
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); ++i) {
				JSONObject object = jsonArray.getJSONObject(i);
				// Create each location object and put there name and city taken from response JSON
				Location location = new Location();
				location.setName(object.getString("name"));
				location.setCity(object.getString("city"));
				result.add(location);
			}
		} catch (JSONException e) {
			Log.e(TAG, "Error during JSON parsing " + e.toString());
			return result;
		}
		return result;
	}
}
