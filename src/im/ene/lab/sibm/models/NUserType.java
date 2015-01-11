package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.DataUtil;

import com.hp.hpl.jena.rdf.model.Resource;

public class NUserType {

	private static final String BASE_PERSON = "http://lab.ene.im/SIBM/thing/usertype/";

	private String name;

	private int accessLevel;

	private Resource resource;

	public NUserType(String name, int level) {
		this.name = name;
		this.accessLevel = level;

		resource = DataUtil.MODEL.createResource(BASE_PERSON + name);
		resource.addLiteral(NProperty.userType, name)
				.addLiteral(NProperty.accessLevel,
						DataUtil.MODEL.createTypedLiteral(level));

	}

	public Resource getResource() {
		return this.resource;
	}

	public static final NUserType ASSISTANT = new NUserType("assistant", 1);
	public static final NUserType EVACUEE = new NUserType("evacuee", 3);
	public static final NUserType VOLUNTEER = new NUserType("volunteer", 2);

	public static final NUserType[] TYPES = { ASSISTANT, EVACUEE, VOLUNTEER };
}
