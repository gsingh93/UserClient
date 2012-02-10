package gsingh.busapp.userclient;

import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

/*
 * To test in the emulator, use the telnet commands:
 * telnet localhost 5554
 * geo fix 30.0 30.0
 */

public class UserClientActivity extends MapActivity {

	private LocationManager locManager = null;
	private LocationListener locListener = null;

	/**
	 * Text view where user sees all information such as latitude, longitude,
	 * and instructions
	 */
	private TextView display = null;
	LinearLayout popup = null;
	boolean popupDisplayed = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		display = (TextView) findViewById(R.id.textview);
		this.popup = (LinearLayout) findViewById(R.id.popupwindow);

		/* Use the LocationManager class to obtain GPS locations */
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locListener = new MyLocationListener(this);
	}

	/**
	 * Click listener for locate button
	 * 
	 * @param v
	 */
	public void onClickLocate(View v) {
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locListener);
		display.setText("Locating...");
	}

	/**
	 * Click listener for draw route button
	 * 
	 * @param v
	 */
	public void onClickDrawRoute(View v) {
		String routeName = (String) ((TextView) this.findViewById(R.id.busname))
				.getText();
		if (routeName == null || routeName == "") {
			Log.d("ERROR", "No routename");
			routeName = "North Commuter";
		} else {
			routeName = routeName.substring(0, routeName.length() - 1);
		}
		Log.d("route name", routeName);
		((MyLocationListener) locListener).drawRoute(routeName);
	}
	
	public void onClickDisplayRoutes(View v) {
		onClickDrawRoute(v);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.routelist:
			intent = new Intent(this, RouteListActivity.class);
			break;
		case R.id.stoplist:
			intent = new Intent(this, StopListActivity.class);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		
		startActivity(intent);
		return true;
	}
}