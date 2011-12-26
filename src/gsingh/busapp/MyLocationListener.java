package gsingh.busapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.TextView;

public class MyLocationListener implements LocationListener {

	/**
	 * URL object of server
	 */
	URL url;

	/**
	 * Address of server
	 */
	String URLText = null;
	
	/**
	 * Information TextView
	 */
	TextView display = null;

	MyLocationListener(TextView display) {
		super();
		this.display = display;
	}

	@Override
	public void onLocationChanged(Location loc) {
		double lat = loc.getLatitude();
		double lon = loc.getLongitude();

		// Create text to be displayed on screen
		String Text = "My current location is: " + "\nLatitude = " + lat
				+ "\nLongitude = " + lon;

		// Display location
		display.setText(Text
				+ "\n\nCheck http://michigangurudwara.com/bus.php to see your location");

		// Send location to server
		URLText = "http://michigangurudwara.com/bus.php?lat=" + lat + "&lon="
				+ lon;
		
		// Set up HTTP objects
		DefaultHttpClient hc = new DefaultHttpClient();
		HttpPost postMethod = new HttpPost(URLText);

		// Send data
		try {
			hc.execute(postMethod);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}