package gsingh.busapp.userclient;

import gsingh.busapp.R;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

/*
 * To test in the emulator, use the telnet commands:
 * telnet localhost 5554
 * geo fix 30.0 30.0
 */

public class BusAppActivity extends MapActivity {

	private LocationManager locManager = null;
	private LocationListener locListener = null;
	private TextView display = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		display = (TextView) findViewById(R.id.textview);

		/* Use the LocationManager class to obtain GPS locations */
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locListener = new MyLocationListener(display);
	}

	public void onClickLocate(View v) {
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				locListener);
		display.setText("Locating...");
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}