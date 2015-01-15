package im.ene.lab.sibm.models;

import im.ene.lab.sibm.map.ksj.Data;
import im.ene.lab.sibm.util.NDataUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class HazardClassification implements Data {

	public static final String BASE_HZTYPE = "http://lab.ene.im/SIBM/thing/hazardclassification/";

	public boolean earthquakeHazard;
	public boolean tsunamiHazard;
	public boolean windAndFloodDamage;
	public boolean volcanicHazard;
	public boolean other;
	public boolean notSpecified;

	private Model model = NDataUtils.createModel();
	private Resource resource;

	private String name;

	public HazardClassification() {
		this.resource = model.createResource();
	}

	public HazardClassification(String name) {
		this.resource = model.createResource(BASE_HZTYPE + name);
	}

	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof String) {
			boolean availability = Boolean.valueOf((String) obj);
			if ("ksj:earthquakeHazard".equals(tag)) {
				this.earthquakeHazard = availability;

			} else if ("ksj:tsunamiHazard".equals(tag)) {
				this.tsunamiHazard = availability;

			} else if ("ksj:windAndFloodDamage".equals(tag)) {
				this.windAndFloodDamage = availability;

			} else if ("ksj:volcanicHazard".equals(tag)) {
				this.volcanicHazard = availability;

			} else if ("ksj:other".equals(tag)) {
				this.other = availability;

			} else if ("ksj:notSpecified".equals(tag)) {
				this.notSpecified = availability;

			}
		}
	}

	public Resource getResource() {
		this.resource.addLiteral(NProperty.earthquakeHazard,
				model.createTypedLiteral(this.earthquakeHazard));
		this.resource.addLiteral(NProperty.tsunamiHazard,
				model.createTypedLiteral(this.tsunamiHazard));
		this.resource.addLiteral(NProperty.windAndFloodDamage,
				model.createTypedLiteral(this.windAndFloodDamage));
		this.resource.addLiteral(NProperty.volcanicHazard,
				model.createTypedLiteral(this.volcanicHazard));
		this.resource.addLiteral(NProperty.other,
				model.createTypedLiteral(this.other));
		this.resource.addLiteral(NProperty.notSpecified,
				model.createTypedLiteral(this.notSpecified));
		return this.resource;
	}

	@Override
	public void setName(String name) {
		this.name = name;

		this.resource = model.createResource(BASE_HZTYPE + name);
	}

	public String getName() {
		return this.name;
	}

}
