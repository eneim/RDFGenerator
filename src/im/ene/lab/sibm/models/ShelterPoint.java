package im.ene.lab.sibm.models;

import im.ene.lab.sibm.map.ksj.Data;
import im.ene.lab.sibm.util.DataUtil;

import com.hp.hpl.jena.rdf.model.Resource;

public class ShelterPoint implements Data {

	public static final String BASE_SHELTER = "http://lab.ene.im/SIBM/thing/shelterpoint/";

	private Resource resource;

	private NGeoPoint geoPoint;

	public NGeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(NGeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	@Override
	public void setName(String name) {
		this.name = name;

		if (this.resource == null)
			this.resource = DataUtil.MODEL.createResource(BASE_SHELTER + name);

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

	public ShelterPoint(String name) {
		this.name = name;
		this.resource = DataUtil.MODEL.createResource(BASE_SHELTER + name);
	}

	public String getName() {
		return this.name;
	}

	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof NGeoPoint) {
			NGeoPoint p = (NGeoPoint) obj;
			this.geoPoint = p;
			this.resource.addProperty(NProperty.geopoint, p.getResource());
		} else if (obj instanceof HazardClassification) {
			this.hazardClassification = (HazardClassification) obj;
			this.resource.addProperty(NProperty.hazardClassification,
					((HazardClassification) obj).getResource());
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:name".equals(tag)) {
				this.name = string;
				this.resource.addLiteral(NProperty.NAME, string);
			} else if ("ksj:address".equals(tag)) {
				this.address = string;
				this.resource.addLiteral(NProperty.address, string);
			} else if ("ksj:facilityType".equals(tag)) {
				this.facilityType = string;
				this.resource.addLiteral(NProperty.facilityType, string);
			} else if ("ksj:seatingCapacity".equals(tag)) {
				this.seatingCapacity = Integer.valueOf(string);
				this.resource.addLiteral(NProperty.seatingCapacity, string);
			} else if ("ksj:facilityScale".equals(tag)) {
				this.facilityScale = Integer.valueOf(string);
				this.resource.addLiteral(NProperty.facilityScale, string);
			} else if ("ksj:administrativeAreaCode".equals(tag)) {
				this.administrativeAreaCode = Integer.valueOf(string);
				this.resource.addLiteral(NProperty.administrativeAreaCode,
						string);
			}
		}
	}

	public Resource getResource() {
		return this.resource;
	}

	@Override
	public String toString() {
		return DataUtil.GSON.toJson(this);
	}
}
