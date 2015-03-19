package im.ene.lab.sibm.models;

import im.ene.lab.sibm.generator.Generator;
import im.ene.lab.sibm.map.ksj.Data;
import im.ene.lab.sibm.util.NDataUtils;

import java.io.File;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.VCARD;

public class ShelterPoint implements Data {

	public static final String BASE_SHELTER = "http://lab.ene.im/SIBM/thing/shelterpoint/";

	private Model model = NDataUtils.createModel();

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
			this.resource = model.createResource(BASE_SHELTER + name);

	}

	// private String position;

	private int administrativeAreaCode;

	private String name;

	private String address;

	private String facilityType;

	private int seatingCapacity;

	private int facilityScale;

	private int storageRice, storageWater, storageMedicine;

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
		this.resource = model.createResource(BASE_SHELTER + name);
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

			this.resource.getModel()
					.add(this.geoPoint.getResource().getModel());
		} else if (obj instanceof HazardClassification) {
			this.hazardClassification = (HazardClassification) obj;
			this.resource.addProperty(NProperty.hazardClassification,
					this.hazardClassification.getResource());

			this.resource.getModel().add(
					this.hazardClassification.getResource().getModel());
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
				int val = Integer.valueOf(string);
				if (val < 0)
					val = 400;
				this.seatingCapacity = val;
				this.resource.addLiteral(NProperty.seatingCapacity,
						model.createTypedLiteral(val));
				// TODO FIXME
				setStorage();
			} else if ("ksj:facilityScale".equals(tag)) {
				this.facilityScale = Integer.valueOf(string);
				this.resource.addLiteral(NProperty.facilityScale,
						model.createTypedLiteral(this.facilityScale));
			} else if ("ksj:administrativeAreaCode".equals(tag)) {
				this.administrativeAreaCode = Integer.valueOf(string);
				this.resource.addLiteral(NProperty.administrativeAreaCode,
						model.createTypedLiteral(this.administrativeAreaCode));
			}
		}
	}

	public Resource getResource() {
		return this.resource;
	}

	@Override
	public String toString() {
		return NDataUtils.GSON.toJson(this);
	}

	private File file = null;

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	private void setStorage() {
		this.storageRice = Generator.genRandomInt(this.seatingCapacity / 2,
				this.seatingCapacity * 4);
		this.resource.addLiteral(NProperty.storageFood,
				model.createTypedLiteral(this.storageRice));
		this.storageWater = Generator.genRandomInt(this.seatingCapacity / 10,
				this.seatingCapacity / 2);
		this.resource.addLiteral(NProperty.storageWater,
				model.createTypedLiteral(this.storageWater));
	}
}
