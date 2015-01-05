package im.ene.lab.rdf.bench.ksj.map.ksj.shelter;

import im.ene.lab.rdf.bench.ksj.map.ksj.Data;
import im.ene.lab.rdf.bench.ksj.map.ksj.NGeoPoint;
import im.ene.lab.rdf.bench.ksj.models.NProperty;
import im.ene.lab.rdf.bench.ksj.models.Prefix;
import im.ene.lab.rdf.bench.ksj.util.DataUtil;

import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class ShelterPoint implements Data {

	public final Prefix prefix = new Prefix("point",
			"http://nlftp.mlit.go.jp/ksj/schemas/ksj-app/point");

	private NGeoPoint geoPoint;

	public NGeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(NGeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	public void setName(String name) {
		this.name = name;
	}

	// private String position;

	private int administrativeAreaCode;

	private String name;

	private String address;

	private String facilityType;

	private int seatingCapacity;

	private int facilityScale;

	private HazardClassification hazardClassification;

	// public String getPosition() {
	// return position;
	// }
	//
	// public void setPosition(String position) {
	// this.position = position;
	// }

	public int getAdministrativeAreaCode() {
		return administrativeAreaCode;
	}

	public void setAdministrativeAreaCode(int administrativeAreaCode) {
		this.administrativeAreaCode = administrativeAreaCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public int getSeatingCapacity() {
		return seatingCapacity;
	}

	public void setSeatingCapacity(int seatingCapacity) {
		this.seatingCapacity = seatingCapacity;
	}

	public int getFacilityScale() {
		return facilityScale;
	}

	public void setFacilityScale(int facilityScale) {
		this.facilityScale = facilityScale;
	}

	public HazardClassification getClassification() {
		return hazardClassification;
	}

	public void setClassification(HazardClassification classification) {
		this.hazardClassification = classification;
	}

	public ShelterPoint() {

	}

	public String getName() {
		return this.name;
	}

	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof NGeoPoint) {
			NGeoPoint p = (NGeoPoint) obj;
			this.geoPoint = p;
		} else if (obj instanceof HazardClassification) {
			this.hazardClassification = (HazardClassification) obj;
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:name".equals(tag)) {
				this.name = string;
			} else if ("ksj:address".equals(tag)) {
				this.address = string;
			} else if ("ksj:facilityType".equals(tag)) {
				this.facilityType = string;
			} else if ("ksj:seatingCapacity".equals(tag)) {
				this.seatingCapacity = Integer.valueOf(string);
			} else if ("ksj:facilityScale".equals(tag)) {
				this.facilityScale = Integer.valueOf(string);
			} else if ("ksj:administrativeAreaCode".equals(tag)) {
				this.administrativeAreaCode = Integer.valueOf(string);
			}
		}
	}

	@Override
	public String toString() {
		return DataUtil.GSON.toJson(this);
	}

	public Model getModel() {
		Resource r_geoPoint = DataUtil.MODEL.createResource();

		for (Entry<Property, Literal> entry : this.geoPoint.properties
				.entrySet()) {
			r_geoPoint.addLiteral(entry.getKey(), entry.getValue());
		}

		Resource r_hazardClassification = DataUtil.MODEL.createResource();

		for (Entry<Property, Literal> entry : this.hazardClassification.properties
				.entrySet()) {
			r_hazardClassification.addLiteral(entry.getKey(), entry.getValue());
		}

		DataUtil.MODEL
				.createResource()
				.addProperty(NProperty.geopoint, r_geoPoint)
				.addProperty(NProperty.hazardClassification,
						r_hazardClassification)
				.addProperty(NProperty.address,
						DataUtil.MODEL.createTypedLiteral(this.address))
				.addProperty(
						NProperty.administrativeAreaCode,
						DataUtil.MODEL
								.createTypedLiteral(this.administrativeAreaCode))
				.addProperty(NProperty.facilityScale,
						DataUtil.MODEL.createTypedLiteral(this.facilityScale))
				.addProperty(NProperty.facilityType,
						DataUtil.MODEL.createTypedLiteral(this.facilityType))
				.addProperty(NProperty.name,
						DataUtil.MODEL.createTypedLiteral(this.name))
				.addProperty(NProperty.seatingCapacity,
						DataUtil.MODEL.createTypedLiteral(this.seatingCapacity));
		return DataUtil.MODEL;
	}
}
