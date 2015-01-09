package im.ene.lab.sibm.map.ksj;

import im.ene.lab.sibm.models.NProperty;
import im.ene.lab.sibm.models.Prefix;
import im.ene.lab.sibm.util.DataUtil;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;

public class NGeoPoint implements Data {

	public Map<Property, Literal> properties = new HashMap<>();

	public final Prefix prefix = new Prefix("geopoint",
			"http://www.opengis.net/gml/3.2/geopoint");

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

			properties.put(NProperty.latitude,
					DataUtil.MODEL.createTypedLiteral(lat));
			properties.put(NProperty.longtitude,
					DataUtil.MODEL.createTypedLiteral(lng));
		}
	}

}
