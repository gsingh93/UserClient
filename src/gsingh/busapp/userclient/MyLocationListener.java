package gsingh.busapp.userclient;

import gsingh.busapp.R;
import gsingh.busapp.userclient.Bus.Stop;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MyLocationListener implements LocationListener {

	/**
	 * Server name
	 */
	private final String SERVERNAME = "http://michigangurudwara.com";

	/**
	 * XML file name
	 */
	private final String FILENAME = "/coord.xml";

	/**
	 * Text view where user sees all information such as latitude, longitude,
	 * and instructions
	 */
	private TextView display = null;

	/**
	 * Map view where user and bus locations will be displayed
	 */
	private MapView mapView = null;

	/**
	 * Map view controller
	 */
	private MapController mapController = null;

	/**
	 * List of overlays on map
	 */
	List<Overlay> mapOverlays = null;

	/**
	 * User overlay items.
	 */
	MyItemizedOverlay userMarkerOverlay = null;

	/**
	 * Bus overlay items.
	 */
	MyItemizedOverlay busMarkerOverlay = null;

	/**
	 * List of all buses/routes in XML file
	 */
	private List<Bus> busList = new LinkedList<Bus>();

	/**
	 * Map of bus/route name to bus object
	 */
	private Map<String, Bus> busMap = new HashMap<String, Bus>();

	/**
	 * Image displayed at user position
	 */
	Drawable userMarker;

	/**
	 * Image displayed at bus position
	 */
	Drawable busMarker;

	/**
	 * Latitude of user
	 */
	double lat;

	/**
	 * Longitude of user
	 */
	double lon;

	MyLocationListener(Activity activity) {
		super();
		this.display = (TextView) activity.findViewById(R.id.textview);
		this.mapView = (MapView) activity.findViewById(R.id.mapview);
		this.userMarker = activity.getResources().getDrawable(
				R.drawable.usermarker);
		this.busMarker = activity.getResources().getDrawable(
				R.drawable.busmarker);

		// Get MapController
		mapController = mapView.getController();

		// Get list of overlays on map
		mapOverlays = mapView.getOverlays();
		userMarkerOverlay = new MyItemizedOverlay(userMarker);
		busMarkerOverlay = new MyItemizedOverlay(busMarker);

		// Set up mapview default settings
		// TODO: Set center to correct location
		mapView.setBuiltInZoomControls(true);
		mapController.setZoom(16);
		GeoPoint center = new GeoPoint((int) (42.2761137 * 1E6),
				(int) (-83.7431708 * 1E6));
		mapController.setCenter(center);

		// Initialize all buses by getting route and stop names from XML file

		// Get all routes from XML file
		NodeList nl = getRouteList();

		String routeName = null;
		NodeList sl = null;
		List<String> stopNames = new LinkedList<String>();
		List<double[]> stopPos = new LinkedList<double[]>();

		// For each route, get all stops
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element route = (Element) nl.item(i);

				routeName = route.getElementsByTagName("name").item(0)
						.getFirstChild().getNodeValue();

				// For each stop, get the stop name and add it to the list
				sl = route.getElementsByTagName("stop");

				if (sl != null && sl.getLength() > 0) {
					for (int j = 0; j < sl.getLength(); j++) {
						Element stop = (Element) sl.item(j);

						stopNames.add(stop.getElementsByTagName("name").item(0)
								.getFirstChild().getNodeValue());
						stopPos.add(new double[] {
								Double.valueOf(stop.getElementsByTagName("lat")
										.item(0).getFirstChild().getNodeValue()),
								Double.valueOf(stop.getElementsByTagName("lon")
										.item(0).getFirstChild().getNodeValue()) });
					}
				}

				// Initialize the bus with the route name and stop list
				initBus(routeName, stopNames, stopPos);
			}
		}

	}

	public void drawRoute() {
		// TODO: This is for route one, need to selectively choose which route
		// somehow

		List<Stop> stops = busList.get(0).getStops();

		for (int i = 0; i < stops.size() - 1; i++) {
			mapOverlays.add(new RouteOverlay(stops.get(i).getPos(), stops.get(
					i + 1).getPos()));
			Log.d("tag", "drawing lines");
		}
	}

	private void initBus(String name, List<String> stopNames,
			List<double[]> stopPos) {
		Bus bus = new Bus(name, stopNames, stopPos);
		busMap.put(name, bus);
		busList.add(bus);
	}

	private NodeList getRouteList() {
		// Setup HTTP connection to XML file
		URL url;
		NodeList nl = null;
		try {
			url = new URL(SERVERNAME + FILENAME);
			InputStream URLStream = url.openStream();

			// Retrieve DOM from XML file
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(URLStream);

			Element docEl = dom.getDocumentElement();

			nl = docEl.getElementsByTagName("route");
		} catch (MalformedURLException e) {
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

		return nl;
	}

	private void updateScreenText() {
		// TODO: Use StringBuilder or format()
		// Create text to be displayed on screen
		String Text = "My current location is: " + "\nLatitude = " + lat
				+ "\nLongitude = " + lon;

		// Display location
		display.setText(Text
				+ "\n\nCheck http://michigangurudwara.com/bus.php to see your location");
	}

	// TODO: Analyze both overlays
	private void updateUserLocation() {
		userMarkerOverlay.clear();

		mapOverlays.remove(userMarkerOverlay);

		GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		OverlayItem overlayItem = new OverlayItem(point, "", "");

		userMarkerOverlay.addOverlay(overlayItem);
		mapOverlays.add(userMarkerOverlay);
		mapController.setCenter(point);

		mapView.invalidate();
	}

	/**
	 * Gets coordinates for all buses
	 */
	private void retreiveBusLocation() {
		// Get list of buses/routes
		NodeList nl = getRouteList();

		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element routeEl = (Element) nl.item(i);
				String name = routeEl.getElementsByTagName("name").item(0)
						.getFirstChild().getNodeValue();

				Bus bus = (Bus) busMap.get(name);

				bus.setPos(
						Double.valueOf(routeEl.getElementsByTagName("lat")
								.item(0).getFirstChild().getNodeValue()),
						Double.valueOf(routeEl.getElementsByTagName("lon")
								.item(0).getFirstChild().getNodeValue()));

				NodeList sl = routeEl.getElementsByTagName("stop");

				if (sl != null && sl.getLength() > 0) {
					for (int j = 0; j < sl.getLength(); j++) {
						Element stopEl = (Element) sl.item(j);

						String stopName = stopEl.getElementsByTagName("name")
								.item(0).getFirstChild().getNodeValue();

						Stop stop = bus.getStop(stopName);

						stop.setArrivalInfo(
								Double.valueOf(stopEl
										.getElementsByTagName("time").item(0)
										.getFirstChild().getNodeValue()),
								Double.valueOf(stopEl
										.getElementsByTagName("distance")
										.item(0).getFirstChild().getNodeValue()));

					}
				}
			}
		}

		/*
		 * String s;
		 * 
		 * InputStream URLStream1 = url.openStream(); InputStreamReader isr =
		 * new InputStreamReader(URLStream1); BufferedReader br = new
		 * BufferedReader(isr);
		 * 
		 * while ((s = br.readLine()) != null) { Log.d("tag1",
		 * String.valueOf(s)); }
		 */

		/*
		 * String response = hc.execute(postMethod, handler); Log.d("tag",
		 * response);
		 */
	}

	public void updateBusLocation() {
		busMarkerOverlay.clear();

		mapOverlays.remove(busMarkerOverlay);

		for (Bus bus : busList) {
			GeoPoint point = new GeoPoint((int) (bus.getPos()[0] * 1E6),
					(int) (bus.getPos()[1] * 1E6));
			OverlayItem overlayItem = new OverlayItem(point, "", "");
			Log.d("tag", String.valueOf(bus.getPos()[0]));
			Log.d("tag", String.valueOf(bus.getPos()[1]));

			busMarkerOverlay.addOverlay(overlayItem);
			mapOverlays.add(busMarkerOverlay);
		}

		mapView.invalidate();
	}

	@Override
	public void onLocationChanged(Location loc) {
		lat = loc.getLatitude();
		lon = loc.getLongitude();

		// Update text on the screen
		updateScreenText();

		// Update user location on map view
		updateUserLocation();

		// TODO: This function should be called separately in regular intervals
		// or wait until one bus location changes
		// Gets bus locations for all buses
		retreiveBusLocation();

		// Update bus location on map view
		updateBusLocation();
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