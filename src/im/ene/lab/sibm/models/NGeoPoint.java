package im.ene.lab.sibm.models;

import im.ene.lab.sibm.map.ksj.Data;
import im.ene.lab.sibm.map.ksj.NPoint;
import im.ene.lab.sibm.util.DataUtil;

import com.hp.hpl.jena.rdf.model.Resource;

public class NGeoPoint implements Data {

	private Resource resource;

	public NGeoPoint() {
		this.resource = DataUtil.MODEL.createResource();
	}

	public NGeoPoint(String name) {
		this.resource = DataUtil.MODEL.createResource(BASE_GEOPOINT + name);
	}

	public static final String BASE_GEOPOINT = "http://lab.ene.im/SIBM/thing/geopoint/";

	private double lat;

	private double lng;

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

	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof NPoint) {
			NPoint point = (NPoint) obj;
			this.lat = point.getX();
			this.lng = point.getY();

			this.resource.addLiteral(NProperty.latitude,
					DataUtil.MODEL.createTypedLiteral(point.getX()));
			this.resource.addLiteral(NProperty.longtitude,
					DataUtil.MODEL.createTypedLiteral(point.getY()));
		}
	}

	public Resource getResource() {
		return this.resource;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;

		if (this.resource == null)
			this.resource = DataUtil.MODEL.createResource(BASE_GEOPOINT + name);
	}

}
