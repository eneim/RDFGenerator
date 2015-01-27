package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.NDataUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class NUserType {

	private static final String BASE_PERSON = "http://lab.ene.im/SIBM/thing/usertype/";

	private String name;

	private int accessLevel;

	private Model model = NDataUtils.createModel();

	private Resource resource;

	public NUserType(String name, int level) {
		this.name = name;
		this.accessLevel = level;

		resource = model.createResource(BASE_PERSON + name);
		resource.addLiteral(NProperty.userType, name).addLiteral(
				NProperty.accessLevel, model.createTypedLiteral(level));

	}

	public Resource getResource() {
		return this.resource;
	}

	@Override
	public String toString() {
		return this.name + " - " + this.accessLevel;
	}

	public static final NUserType ASSISTANT = new NUserType("assistant", 1);
	public static final NUserType EVACUEE = new NUserType("evacuee", 3);
	public static final NUserType VOLUNTEER = new NUserType("volunteer", 2);

	public static final NUserType[] TYPES = { ASSISTANT, EVACUEE, VOLUNTEER };
}
