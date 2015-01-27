package im.ene.lab.sibm.models;

import im.ene.lab.sibm.map.ksj.Data;
import im.ene.lab.sibm.map.ksj.NPoint;
import im.ene.lab.sibm.util.NDataUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class NGeoPoint implements Data {

	private Resource resource;

	private Model model = NDataUtils.createModel();

	public NGeoPoint() {
		this.resource = model.createResource();
	}

	public NGeoPoint(String name) {
		this.resource = model.createResource(BASE_GEOPOINT + name);
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

		}
	}

	public Resource getResource() {
		this.resource.addLiteral(NProperty.latitude,
				model.createTypedLiteral(this.lat));
		this.resource.addLiteral(NProperty.longtitude,
				model.createTypedLiteral(this.lng));
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
			this.resource = model.createResource(BASE_GEOPOINT + name);
	}

}
