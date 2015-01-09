package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.DataUtil;

import com.hp.hpl.jena.rdf.model.Resource;

public class Prefecture {

	public static final String BASE_PREF = "http://lab.ene.im/SIBM/thing/prefecture/";

	private Resource resource;

	private final int code;

	private final String name;

	private ShelterPoint[] shelterPoints;

	public ShelterPoint[] getShelterPoints() {
		return shelterPoints;
	}

	public void setShelterPoints(ShelterPoint[] shelterPoints) {
		this.shelterPoints = shelterPoints;

		if (shelterPoints == null || shelterPoints.length < 1)
			return;

		for (ShelterPoint point : shelterPoints) {
			this.resource.addProperty(NProperty.hasShelterPoint,
					point.getResource());
		}
	}

	public Prefecture(String name, int code) {
		this.name = name;
		this.code = code;

		this.resource = DataUtil.MODEL.createResource(BASE_PREF + name);
		this.resource.addLiteral(NProperty.CODE, code);
		this.resource.addLiteral(NProperty.NAME, name);
	}

	public int getCode() {
		return this.code;
	}

	public String getName() {
		return this.name;
	}

	public Resource getResource() {
		return this.resource;
	}

}
