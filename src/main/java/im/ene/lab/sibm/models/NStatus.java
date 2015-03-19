package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.NDataUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class NStatus {

	private static final String BASE_STATUS = "http://lab.ene.im/SIBM/thing/status/";

	private String name;

	private int level;

	private Model model = NDataUtils.createModel();

	private Resource resource;

	public NStatus(String name, int level) {
		this.name = name;
		this.level = level;

		resource = model.createResource(BASE_STATUS + name);
		resource.addLiteral(NProperty.statusName, name).addLiteral(
				NProperty.statusLevel, model.createTypedLiteral(level));

	}

	public Resource getResource() {
		return this.resource;
	}

	@Override
	public String toString() {
		return this.name + " - " + this.level;
	}

	public static final NStatus NORMAL = new NStatus("normal", 0);
	public static final NStatus MINOR = new NStatus("minor", 1);
	public static final NStatus MODERATE = new NStatus("moderate", 2);
	public static final NStatus SERIOUS = new NStatus("serious", 3);

	public static NStatus[] STATUSES = { NORMAL, MINOR, MODERATE, SERIOUS };
}
