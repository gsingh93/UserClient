package gsingh.busapp.userclient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Bus {

	public class Stop {
		private String name;

		private double lat;
		private double lon;

		private double arrivalTime;
		private double arrivalDistance;

		public Stop(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public double[] getPos() {
			return new double[] { lat, lon };
		}

		public void setPos(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
		}

		public double getArrivalTime() {
			return arrivalTime;
		}

		public double getArrivalDistance() {
			return arrivalDistance;
		}

		public void setArrivalInfo(double time, double distance) {
			arrivalTime = time;
			arrivalDistance = distance;
		}
	}

	private String name;

	private double lat;
	private double lon;

	private List<Stop> stopList = new LinkedList<Stop>();

	private Map<String, Stop> stopMap = new HashMap<String, Stop>();

	Bus(String name, List<String> stopNames) {
		this.name = name;

		for (String stopName : stopNames) {
			Stop stop = new Stop(stopName);
			this.stopList.add(stop);
			stopMap.put(stopName, stop);
		}
	}

	public String getName() {
		return name;
	}

	public double[] getPos() {
		return new double[] { lat, lon };
	}

	public void setPos(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public List<String> getStopNames() {
		List<String> stopNames = new LinkedList<String>();
		for (Stop stop : stopList) {
			stopNames.add(stop.getName());
		}
		return stopNames;
	}

	public List<Double> getStopTimes() {
		List<Double> stopTimes = new LinkedList<Double>();
		for (Stop stop : stopList) {
			stopTimes.add(stop.getArrivalTime());
		}
		return stopTimes;
	}

	public List<Double> getStopDistances() {
		List<Double> stopDistances = new LinkedList<Double>();
		for (Stop stop : stopList) {
			stopDistances.add(stop.getArrivalDistance());
		}
		return stopDistances;
	}

	public List<Stop> getStops() {
		return stopList;
	}

	public Stop getStop(String stopName) {
		return stopMap.get(stopName);
	}
}
