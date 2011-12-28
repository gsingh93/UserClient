package gsingh.busapp.userclient;

import java.util.LinkedList;
import java.util.List;

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

		public double[] getArrivalInfo() {
			return new double[] { arrivalTime, arrivalDistance };
		}
		
		public void setArrivalInfo(double time, double distance) {
			arrivalTime = time;
			arrivalDistance = distance;
		}
	}

	private String name;

	private double lat;
	private double lon;

	List<Stop> stops = new LinkedList<Stop>();
	
	Bus(String name, List<String> stopNames) {
		this.name = name;

		for (String stopName : stopNames) {
			this.stops.add(new Stop(stopName));
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

	public List<Stop> getStops() {
		return stops;
	}

}
