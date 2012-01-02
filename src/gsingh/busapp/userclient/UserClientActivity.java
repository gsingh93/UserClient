package gsingh.busapp.userclient;

import gsingh.busapp.R;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

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

	/**
	 * Map view where user and bus locations will be displayed
	 */
	private MapView mapView = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		display = (TextView) findViewById(R.id.textview);
		mapView = (MapView) findViewById(R.id.mapview);

		mapView.setBuiltInZoomControls(true);

		GeoPoint center = new GeoPoint((int) (42.2761137 * 1E6),
				(int) (-83.7431708 * 1E6));
		mapView.getController().setCenter(center);
		mapView.getController().setZoom(16);

		/* Use the LocationManager class to obtain GPS locations */
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locListener = new MyLocationListener(this);
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