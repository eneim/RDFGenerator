package im.ene.lab.sibm.models;

import org.geotools.referencing.GeodeticCalculator;

public class NPoint {

	private double lat;

	private double lng;

	public NPoint(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public static double distance(NPoint p1, NPoint p2) {
		GeodeticCalculator calc = new GeodeticCalculator();
		calc.setStartingGeographicPoint(p1.lng, p1.lat);
		calc.setDestinationGeographicPoint(p2.lng, p2.lat);
		// return Math.sqrt((p1.lat - p2.lat) * (p1.lat - p2.lat)
		// + (p1.lng - p2.lng) * (p1.lng - p2.lng));
		double dis = calc.getOrthodromicDistance();
		// System.out.println(dis);
		return dis;
	}

	public static double distance(NGeoPoint gp1, NPoint p2) {
		NPoint p1 = new NPoint(gp1.getLat(), gp1.getLng());
		return distance(p1, p2);
	}
}
