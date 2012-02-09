package gsingh.busapp.userclient.components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint;

public class Route {

	private List<Stop> stops = new LinkedList<Stop>();
	private List<Bus> buses = new LinkedList<Bus>();
	private List<GeoPoint> geoPoints = new LinkedList<GeoPoint>();

	private String name = null;

	private Map<String, Stop> stopMap = new HashMap<String, Stop>();

	public Route(String name, List<String> stopNames, List<double[]> stopPos,
			List<double[]> routeGP) {

		this.name = name;

		int i = 0;
		for (String stopName : stopNames) {
			Stop stop = new Stop(stopName);
			stop.setPos(stopPos.get(i)[0], stopPos.get(i)[1]);
			this.stops.add(stop);
			stopMap.put(stopName, stop);
			i++;
		}

		for (double[] geoPoint : routeGP) {
			geoPoints.add(constructGeoPoint(geoPoint[0], geoPoint[1]));
		}
	}

	private GeoPoint constructGeoPoint(double lat, double lon) {
		return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
	}

	public void drawRoute() {

	}

	public List<String> getStopNames() {
		List<String> stopNames = new LinkedList<String>();
		for (Stop stop : stops) {
			stopNames.add(stop.getName());
		}
		return stopNames;
	}

	public List<Double> getStopTimes() {
		List<Double> stopTimes = new LinkedList<Double>();
		for (Stop stop : stops) {
			stopTimes.add(stop.getArrivalTime());
		}
		return stopTimes;
	}

	public List<Double> getStopDistances() {
		List<Double> stopDistances = new LinkedList<Double>();
		for (Stop stop : stops) {
			stopDistances.add(stop.getArrivalDistance());
		}
		return stopDistances;
	}

	public String getName() {
		return name;
	}

	public Stop getStop(String stopName) {
		return stopMap.get(stopName);
	}

	public void addBus(Bus bus) {
		buses.add(bus);
	}

	public List<GeoPoint> getGeoPoints() {
		return geoPoints;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public List<Bus> getBuses() {
		return buses;
	}
}
