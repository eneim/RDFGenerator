package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.DataUtil;

import javax.annotation.Generated;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

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

	private Model model = DataUtil.createModel();

	public Resource getResource() {
		this.resource = model.createResource(BASE_PERSON + profile.getUserID());

		Resource res = ModelFactory.createDefaultModel().createResource();
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
		
		this.resource.addProperty(NProperty.profile, res).addProperty(RDF.type,
				getType().getResource());
		this.resource.getModel().add(res.getModel())
				.add(getType().getResource().getModel());

		return this.resource;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
