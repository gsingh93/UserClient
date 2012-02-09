package gsingh.busapp.userclient.components;


import com.google.android.maps.GeoPoint;

public class Bus {

	private String name;

	private GeoPoint pos;

	public Bus(String name) {
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
}
