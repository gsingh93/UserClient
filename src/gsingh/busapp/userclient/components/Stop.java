package gsingh.busapp.userclient.components;

import com.google.android.maps.GeoPoint;

public class Stop {
	private String name;

	private GeoPoint pos;

	private double arrivalTime;
	private double arrivalDistance;

	public Stop(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public GeoPoint getPos() {
		return pos;
	}

	public void setPos(GeoPoint pos) {
		this.pos = pos;
	}

	public void setPos(double lat, double lon) {
		this.pos = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
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