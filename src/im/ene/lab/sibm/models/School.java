package im.ene.lab.sibm.models;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import im.ene.lab.sibm.map.ksj.Data;
import im.ene.lab.sibm.util.NDataUtils;

public class School implements Data {

	public static final String BASE_SCHOOL = "http://lab.ene.im/SIBM/thing/school/";

	private Model model = NDataUtils.createModel();

	private Resource resource;

	private NGeoPoint geoPoint;

	private String name;

	private String address;

	private int administrativeArea;

	private SchoolType type;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAdministrativeArea() {
		return administrativeArea;
	}

	public void setAdministrativeArea(int administrativeArea) {
		this.administrativeArea = administrativeArea;
	}

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
			this.resource = model.createResource(BASE_SCHOOL + name);
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
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:name".equals(tag)) {
				this.name = string;
				setType(string);
				this.resource.addLiteral(NProperty.NAME, string);
			} else if ("ksj:address".equals(tag)) {
				this.address = string;
				this.resource.addLiteral(NProperty.address, string);
			} else if ("ksj:administrativeArea".equals(tag)) {
				this.administrativeArea = Integer.valueOf(string);
				this.resource.addLiteral(NProperty.administrativeAreaCode,
						model.createTypedLiteral(this.administrativeArea));
			}
		}
	}

	private void setType(String string) {
		if (string.contains(SchoolType.UNI.getType())) {
			this.type = SchoolType.UNI;
		} else if (string.contains(SchoolType.COL.getType())) {
			this.type = SchoolType.COL;
		} else if (string.contains(SchoolType.HIGH.getType())) {
			this.type = SchoolType.HIGH;
		} else if (string.contains(SchoolType.MID.getType())) {
			this.type = SchoolType.MID;
		} else if (string.contains(SchoolType.ELE.getType())) {
			this.type = SchoolType.ELE;
		} else if (string.contains(SchoolType.SPEC.getType())) {
			this.type = SchoolType.SPEC;
		} else
			this.type = SchoolType.OTH;
	}

	public SchoolType getType() {
		return this.type;
	}

	public String getTypeString() {
		return this.getType().getType();
	}

	@Override
	public String toString() {
		return this.getName() + " - " + this.getAddress();
	}

	public static enum SchoolType {
		UNI("大学"), COL("工業高等専門学校"), HIGH("高等学校"), MID("中学校"), ELE("小学校"), SPEC(
				"特別支援学校"), OTH("その他");

		private String type;

		private SchoolType(String type) {
			this.type = type;
		}

		public String getType() {
			return this.type;
		}
	}
}
