package gsingh.busapp.userclient;

import gsingh.busapp.R;
import gsingh.busapp.userclient.components.Bus;
import gsingh.busapp.userclient.components.Route;
import gsingh.busapp.userclient.components.Stop;
import gsingh.busapp.userclient.overlay.BusOverlay;
import gsingh.busapp.userclient.overlay.BusOverlayItem;
import gsingh.busapp.userclient.overlay.MyItemizedOverlay;
import gsingh.busapp.userclient.overlay.RouteOverlay;
import gsingh.busapp.userclient.overlay.StopsOverlay;

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
	 * Stop overlay items.
	 */
	MyItemizedOverlay stopMarkerOverlay = null;

	/**
	 * List of all routes in XML file
	 */
	private List<Route> routeList = new LinkedList<Route>();

	/**
	 * Map of bus/route name to bus object
	 */
	private Map<String, Route> routeMap = new HashMap<String, Route>();

	/**
	 * Image displayed at user position
	 */
	Drawable userMarker;

	/**
	 * Image displayed at bus position
	 */
	Drawable busMarker;

	/**
	 * True when route is shown, false otherwise
	 */
	Boolean routeDisplayed = false;

	/**
	 * Latitude of user
	 */
	double lat;

	/**
	 * Longitude of user
	 */
	double lon;

	List<RouteOverlay> routeOverlays = new LinkedList<RouteOverlay>();

	MyLocationListener(Activity activity) {
		super();
		this.display = (TextView) activity.findViewById(R.id.textview);
		this.mapView = (MapView) activity.findViewById(R.id.mapview);
		this.userMarker = activity.getResources().getDrawable(
				R.drawable.usermarker);
		this.busMarker = activity.getResources().getDrawable(R.drawable.bus);

		// Get MapController
		mapController = mapView.getController();

		// Get list of overlays on map
		mapOverlays = mapView.getOverlays();

		// Initialize overlays
		userMarkerOverlay = new MyItemizedOverlay(userMarker);
		busMarkerOverlay = new BusOverlay(busMarker, activity);
		stopMarkerOverlay = new StopsOverlay(userMarker, activity);

		// Initialize default MapView settings
		mapView.setBuiltInZoomControls(true);
		mapController.setZoom(16);
		GeoPoint center = new GeoPoint((int) (42.27778 * 1E6),
				(int) (-83.73503 * 1E6));
		mapController.setCenter(center);

		// TODO: Refactor
		// Initialize all buses by getting route and stop names from XML file

		// Get all routes from XML file
		NodeList rl = getRouteList();

		String routeName = null;
		NodeList sl = null;
		List<String> stopNames = new LinkedList<String>();
		List<double[]> stopPos = new LinkedList<double[]>();
		List<double[]> routeGP = new LinkedList<double[]>();

		// For each route, get all stops
		if (rl != null && rl.getLength() > 0) {
			for (int i = 0; i < rl.getLength(); i++) {
				Element route = (Element) rl.item(i);

				// Get the name of the route
				routeName = route.getElementsByTagName("name").item(0)
						.getFirstChild().getNodeValue();

				// For each stop, get the stop name and add it to the list
				sl = route.getElementsByTagName("stop");

				if (sl != null && sl.getLength() > 0) {
					for (int j = 0; j < sl.getLength(); j++) {
						Element stop = (Element) sl.item(j);

						// Add the stop name to the list
						stopNames.add(stop.getElementsByTagName("name").item(0)
								.getFirstChild().getNodeValue());

						// Add the position of the stop to the list
						stopPos.add(new double[] {
								Double.valueOf(stop.getElementsByTagName("lat")
										.item(0).getFirstChild().getNodeValue()),
								Double.valueOf(stop.getElementsByTagName("lon")
										.item(0).getFirstChild().getNodeValue()) });
					}
				}

				// Get route geopoints
				NodeList gpl = ((Element) route.getElementsByTagName("routegp")
						.item(0)).getElementsByTagName("gp");

				if (gpl != null && gpl.getLength() > 0) {
					for (int j = 0; j < gpl.getLength(); j++) {
						Element gp = (Element) gpl.item(j);

						routeGP.add(new double[] {
								Double.valueOf(gp.getElementsByTagName("lat")
										.item(0).getFirstChild().getNodeValue()),
								Double.valueOf(gp.getElementsByTagName("lon")
										.item(0).getFirstChild().getNodeValue()) });

						Log.d("lat", String.valueOf(gp
								.getElementsByTagName("lat").item(0)
								.getFirstChild().getNodeValue()));
					}
				}

				// Initialize the bus with the route name and stop list
				initRoute(routeName, stopNames, stopPos, routeGP);
			}
		}

		// TODO: coord.xml will have bus tag, but for now, just make one bus
		routeList.get(0).addBus(new Bus("North Commuter1"));
	}

	// TODO: Reuse old route if not updated
	// TODO: Refactor to Route class
	public void drawRoute() {
		// TODO: This is for route one, need to selectively choose which route
		// somehow

		Route route = routeList.get(0);
		if (routeDisplayed == false) {
			List<Stop> stops = route.getStops();
			List<GeoPoint> routeGP = route.getGeoPoints();

			Log.d("tag", String.valueOf(routeGP.size()));

			// Adds route GeoPoints to routeOverlays
			for (int i = 0; i < routeGP.size() - 1; i++) {
				Log.d("lat", String.valueOf(routeGP.get(i).getLatitudeE6()));
				routeOverlays.add(new RouteOverlay(routeGP.get(i), routeGP
						.get(i + 1)));
			}

			// Adds stop GeoPoints to stopMarkerOverlay
			for (Stop stop : stops) {
				stopMarkerOverlay.addOverlay(new OverlayItem(stop.getPos(),
						stop.getName(), stop.getName()));
			}

			mapOverlays.add(stopMarkerOverlay);
			mapOverlays.addAll(routeOverlays);
			routeDisplayed = true;
		} else {
			mapOverlays.remove(stopMarkerOverlay);
			mapOverlays.removeAll(routeOverlays);
			routeDisplayed = false;
		}
		mapView.invalidate();
	}

	private void initRoute(String name, List<String> stopNames,
			List<double[]> stopPos, List<double[]> routeGP) {
		Route route = new Route(name, stopNames, stopPos, routeGP);
		routeMap.put(name, route);
		routeList.add(route);
	}

	private NodeList getRouteList() {
		// Setup HTTP connection to XML file
		URL url;
		NodeList rl = null;
		try {
			url = new URL(SERVERNAME + FILENAME);
			InputStream URLStream = url.openStream();

			// Retrieve DOM from XML file
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(URLStream);

			Element docEl = dom.getDocumentElement();

			rl = docEl.getElementsByTagName("route");
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

		return rl;
	}

	private void updateScreenText() {
		// Create text to be displayed on screen
		StringBuilder sb = new StringBuilder("My current location is: ");
		sb.append("\nLatitude = ").append(lat);
		sb.append("\nLongitude = ").append(lon);
		sb.append("\n\nCheck").append(SERVERNAME).append("/bus.php ")
				.append(" to see your location");

		// Display location
		display.setText(sb.toString());
	}

	// TODO: Analyze both overlays to make sure no commands are wasted
	/**
	 * Updates user location on map and centers on location
	 */
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
		// Get list of routes
		NodeList rl = getRouteList();

		// TODO: Refactor
		if (rl != null && rl.getLength() > 0) {
			for (int i = 0; i < rl.getLength(); i++) {
				Element routeEl = (Element) rl.item(i);
				String name = routeEl.getElementsByTagName("name").item(0)
						.getFirstChild().getNodeValue();

				Route route = (Route) routeMap.get(name);

				// TODO: Get each bus instead of first one
				Bus bus = route.getBuses().get(0);

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

						Stop stop = route.getStop(stopName);

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

	/**
	 * Updates bus location on map
	 */
	public void updateBusLocation() {
		busMarkerOverlay.clear();

		mapOverlays.remove(busMarkerOverlay);

		Log.d("tag", String.valueOf(routeList.size()));

		for (Route route : routeList) {
			for (Bus bus : route.getBuses()) {
				GeoPoint point = bus.getPos();
				BusOverlayItem overlayItem = new BusOverlayItem(point,
						bus.getName(), bus.getName());
				Log.d("tag", String.valueOf(point.getLatitudeE6()));
				Log.d("tag", String.valueOf(point.getLongitudeE6()));

				busMarkerOverlay.addOverlay(overlayItem);
				mapOverlays.add(busMarkerOverlay);
			}
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