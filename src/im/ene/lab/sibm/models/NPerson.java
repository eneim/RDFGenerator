package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.DataUtil;

import javax.annotation.Generated;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.VCARD;

@Generated("org.jsonschema2pojo")
public class NPerson {

	private static final String BASE_PERSON = "http://lab.ene.im/SIBM/person/";

	protected Resource resource;

	private NUserType type;

	public NUserType getType() {
		return type;
	}

	public void setType(NUserType type) {
		this.type = type;
	}

	private Profile profile;

	/**
	 * 
	 * @return The profile
	 */
	public Profile getProfile() {
		return profile;
	}

	/**
	 * 
	 * @param profile
	 *            The profile
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Resource getResource() {
		this.resource = DataUtil.MODEL.createResource(BASE_PERSON
				+ profile.getUserID());

		Resource res = DataUtil.MODEL.createResource();
		res.addLiteral(NProperty.userID, profile.getUserID())
				.addLiteral(NProperty.firstName, profile.getFirstName())
				.addLiteral(NProperty.surname, profile.getSurname())
				.addLiteral(NProperty.gender, profile.getGender())
				.addLiteral(NProperty.birthday, profile.getBirthday())
				.addLiteral(NProperty.age, profile.getAge())
				.addLiteral(NProperty.phone, profile.getPhone())
				// .addLiteral(NProperty.address, profile.getAddress())
				// .addLiteral(NProperty.zipCode, profile.getZipCode())
				.addLiteral(NProperty.email, profile.getEmail())
		// .addLiteral(NProperty.occupation, profile.getOccupation())
		;
		this.resource.addProperty(NProperty.profile, res).addProperty(
				RDF.type, getType().getResource());

		return this.resource;
	}
}
