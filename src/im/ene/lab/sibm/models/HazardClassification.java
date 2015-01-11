package im.ene.lab.sibm.models;

import im.ene.lab.sibm.map.ksj.Data;
import im.ene.lab.sibm.util.DataUtil;

import com.hp.hpl.jena.rdf.model.Resource;

public class HazardClassification implements Data {

	public static final String BASE_HZTYPE = "http://lab.ene.im/SIBM/thing/hazardclassification/";

	public boolean earthquakeHazard;
	public boolean tsunamiHazard;
	public boolean windAndFloodDamage;
	public boolean volcanicHazard;
	public boolean other;
	public boolean notSpecified;

	private Resource resource;

	private String name;

	public HazardClassification() {
		this.resource = DataUtil.MODEL.createResource();
	}

	public HazardClassification(String name) {
		this.resource = DataUtil.MODEL.createResource(BASE_HZTYPE + name);
	}

	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof String) {
			boolean availability = Boolean.valueOf((String) obj);
			if ("ksj:earthquakeHazard".equals(tag)) {
				this.earthquakeHazard = availability;
				this.resource.addLiteral(NProperty.earthquakeHazard,
						DataUtil.MODEL.createTypedLiteral(availability));
			} else if ("ksj:tsunamiHazard".equals(tag)) {
				this.tsunamiHazard = availability;
				this.resource.addLiteral(NProperty.tsunamiHazard,
						DataUtil.MODEL.createTypedLiteral(availability));
			} else if ("ksj:windAndFloodDamage".equals(tag)) {
				this.windAndFloodDamage = availability;
				this.resource.addLiteral(NProperty.windAndFloodDamage,
						DataUtil.MODEL.createTypedLiteral(availability));
			} else if ("ksj:volcanicHazard".equals(tag)) {
				this.volcanicHazard = availability;
				this.resource.addLiteral(NProperty.volcanicHazard,
						DataUtil.MODEL.createTypedLiteral(availability));
			} else if ("ksj:other".equals(tag)) {
				this.other = availability;
				this.resource.addLiteral(NProperty.other,
						DataUtil.MODEL.createTypedLiteral(availability));
			} else if ("ksj:notSpecified".equals(tag)) {
				this.notSpecified = availability;
				this.resource.addLiteral(NProperty.notSpecified,
						DataUtil.MODEL.createTypedLiteral(availability));
			}
		}
	}

	public Resource getResource() {
		return this.resource;
	}

	@Override
	public void setName(String name) {
		this.name = name;

		if (this.resource == null)
			this.resource = DataUtil.MODEL.createResource(BASE_HZTYPE + name);
	}

	public String getName() {
		return this.name;
	}

}
