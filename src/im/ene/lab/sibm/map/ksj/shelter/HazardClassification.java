package im.ene.lab.sibm.map.ksj.shelter;

import im.ene.lab.sibm.map.ksj.Data;
import im.ene.lab.sibm.models.NProperty;
import im.ene.lab.sibm.models.Prefix;
import im.ene.lab.sibm.util.DataUtil;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;

public class HazardClassification implements Data {

	public Map<Property, Literal> properties = new HashMap<>();

	public final Prefix prefix = new Prefix("hazardtype",
			"http://nlftp.mlit.go.jp/ksj/schemas/ksj-app/hazardtype");

	public boolean earthquakeHazard;
	public boolean tsunamiHazard;
	public boolean windAndFloodDamage;
	public boolean volcanicHazard;
	public boolean other;
	public boolean notSpecified;

	public HazardClassification() {

	}

	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof String) {
			boolean availability = Boolean.valueOf((String) obj);
			if ("ksj:earthquakeHazard".equals(tag)) {
				this.earthquakeHazard = availability;

				properties.put(NProperty.earthquakeHazard, DataUtil.MODEL
						.createTypedLiteral(this.earthquakeHazard));
			} else if ("ksj:tsunamiHazard".equals(tag)) {
				this.tsunamiHazard = availability;

				properties.put(NProperty.tsunamiHazard,
						DataUtil.MODEL.createTypedLiteral(this.tsunamiHazard));
			} else if ("ksj:windAndFloodDamage".equals(tag)) {
				this.windAndFloodDamage = availability;

				properties.put(NProperty.windAndFloodDamage, DataUtil.MODEL
						.createTypedLiteral(this.windAndFloodDamage));
			} else if ("ksj:volcanicHazard".equals(tag)) {
				this.volcanicHazard = availability;

				properties.put(NProperty.volcanicHazard,
						DataUtil.MODEL.createTypedLiteral(this.volcanicHazard));
			} else if ("ksj:other".equals(tag)) {
				this.other = availability;

				properties.put(NProperty.other,
						DataUtil.MODEL.createTypedLiteral(this.other));
			} else if ("ksj:notSpecified".equals(tag)) {
				this.notSpecified = availability;

				properties.put(NProperty.notSpecified,
						DataUtil.MODEL.createTypedLiteral(this.notSpecified));
			}
		}
	}

}
