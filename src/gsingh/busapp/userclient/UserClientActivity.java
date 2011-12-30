package gsingh.busapp.userclient;

import gsingh.busapp.R;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

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

	private MapView mapView = null;

	List<Overlay> mapOverlays;
	Drawable drawable;
	MyItemizedOverlay itemizedOverlay;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		display = (TextView) findViewById(R.id.textview);
		mapView = (MapView) findViewById(R.id.mapview);

		mapView.setBuiltInZoomControls(true);
		mapOverlays = mapView.getOverlays();
		drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		itemizedOverlay = new MyItemizedOverlay(drawable);

		GeoPoint center = new GeoPoint((int)(42.2761137*1E6), (int)(-83.7431708*1E6));
		mapView.getController().setCenter(center);
		mapView.getController().setZoom(16);
		OverlayItem overlayItem = new OverlayItem(center, "", "");

		itemizedOverlay.addOverlay(overlayItem);
		mapOverlays.add(itemizedOverlay);

		/* Use the LocationManager class to obtain GPS locations */
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// TODO: Only pass context
		locListener = new MyLocationListener(display, mapView, drawable);
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