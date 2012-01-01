package gsingh.busapp.userclient;

import gsingh.busapp.R;
import gsingh.busapp.userclient.Bus.Stop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

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
	MapView mapView = null;

	/**
	 * Latitude of user
	 */
	double lat;

	/**
	 * Longitude of user
	 */
	double lon;

	/**
	 * List of all buses/routes in XML file
	 */
	private List<Bus> busList = new LinkedList<Bus>();

	Drawable drawable = null;

	MyLocationListener(Activity activity) {
		super();
		this.display = (TextView) activity.findViewById(R.id.textview);
		this.mapView = (MapView) activity.findViewById(R.id.mapview);
		this.drawable = activity.getResources().getDrawable(
				R.drawable.androidmarker);

		// TODO: Get all buses from XML file
		// Initialize all buses
		List<String> stopNames = new LinkedList<String>();

		stopNames.add("Stop1");
		stopNames.add("Stop2");
		stopNames.add("Stop3");

		initBus("North Commuter", stopNames);

	}

	private void initBus(String name, List<String> stopNames) {
		Bus bus = new Bus(name, stopNames);
		busList.add(bus);
	}

	private void updateScreenText() {
		// Create text to be displayed on screen
		String Text = "My current location is: " + "\nLatitude = " + lat
				+ "\nLongitude = " + lon;

		// Display location
		display.setText(Text
				+ "\n\nCheck http://michigangurudwara.com/bus.php to see your location");

		List<Overlay> mapOverlays = mapView.getOverlays();

		MyItemizedOverlay itemizedOverlay = new MyItemizedOverlay(drawable);

		GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		OverlayItem overlayItem = new OverlayItem(point, "", "");

		itemizedOverlay.addOverlay(overlayItem);
		mapOverlays.add(itemizedOverlay);

		Log.d("tag", "done");
	}

	// NOTE: This should be in BusClient. UserClient should only store location
	// locally, not send it to server. In this prototype, user location
	// perceived as a bus.
	private void updateUserLocation() {
		// TODO: Add route name
		// Location GET URL
		URLText = "http://michigangurudwara.com/bus.php?lat=" + lat + "&lon="
				+ lon;

		mapView.setBuiltInZoomControls(true);
		List<Overlay> mapOverlays = mapView.getOverlays();

		MyItemizedOverlay itemizedOverlay = new MyItemizedOverlay(drawable);

		GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		OverlayItem overlayItem = new OverlayItem(point, "", "");

		itemizedOverlay.addOverlay(overlayItem);
		mapOverlays.add(itemizedOverlay);
	}

	private void retreiveBusLocation() {

		// TODO: Get list of buses
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// Send XML
		try {
			String s;
			URL url = new URL("http://michigangurudwara.com/coord.xml");

			InputStream URLStream2 = url.openStream();

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(URLStream2);

			Element docEl = dom.getDocumentElement();

			NodeList nl = docEl.getElementsByTagName("route");

			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					Element route = (Element) nl.item(i);
					String name = route.getElementsByTagName("name").item(0)
							.getFirstChild().getNodeValue();

					for (Bus bus : busList) {
						if (bus.getName().equals(name)) {
							bus.setPos(
									Double.valueOf(route
											.getElementsByTagName("lat")
											.item(0).getFirstChild()
											.getNodeValue()),
									Double.valueOf(route
											.getElementsByTagName("lon")
											.item(0).getFirstChild()
											.getNodeValue()));

							NodeList sl = route.getElementsByTagName("stop");

							if (sl != null && sl.getLength() > 0) {
								for (int j = 0; j < sl.getLength(); j++) {
									Element stopEl = (Element) sl.item(j);
									String stopName = stopEl
											.getElementsByTagName("name")
											.item(0).getFirstChild()
											.getNodeValue();

									for (Stop stop : bus.getStops()) {
										if (stopName.equals(stop.getName())) {

											stop.setArrivalInfo(
													Double.valueOf(stopEl
															.getElementsByTagName(
																	"time")
															.item(0)
															.getFirstChild()
															.getNodeValue()),
													Double.valueOf(stopEl
															.getElementsByTagName(
																	"distance")
															.item(0)
															.getFirstChild()
															.getNodeValue()));
										}
									}
								}
							}
						}
					}

				}
			}

			InputStream URLStream1 = url.openStream();
			InputStreamReader isr = new InputStreamReader(URLStream1);
			BufferedReader br = new BufferedReader(isr);

			while ((s = br.readLine()) != null) {
				Log.d("tag1", String.valueOf(s));
			}

			/*
			 * String response = hc.execute(postMethod, handler); Log.d("tag",
			 * response);
			 */
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onLocationChanged(Location loc) {
		lat = loc.getLatitude();
		lon = loc.getLongitude();

		// Update text on the screen
		updateScreenText();

		// Send location data to server
		updateUserLocation();

		// TODO: This function should be called separately in regular intervals
		// NOTE: In this prototype, the user location is perceived as a bus.
		// This eliminates the need of having another client broadcasting it's
		// position
		retreiveBusLocation();

		for (Bus bus : busList) {

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