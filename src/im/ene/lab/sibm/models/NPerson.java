package im.ene.lab.sibm.models;

import im.ene.lab.sibm.models.School.SchoolType;
import im.ene.lab.sibm.util.NDataUtils;

import javax.annotation.Generated;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;

@Generated("org.jsonschema2pojo")
public class NPerson {

	private static final String BASE_PERSON = "http://lab.ene.im/SIBM/person/";

	protected Resource resource;

	public NPerson(String id) {
		this.ID = id;
		this.resource = model.createResource(BASE_PERSON + id);
	}

	private String ID;

	public String getID() {
		return ID;
	}

	private SchoolType schoolType;

	public SchoolType getSchoolType() {
		return schoolType;
	}

	public void setSchoolType(SchoolType schoolType) {
		this.schoolType = schoolType;
	}

	public void setID(String iD) {
		ID = iD;
	}

	// private NStatus status;

	private NLabel disease;

	// public NStatus getStatus() {
	// return status;
	// }
	//
	// public void setStatus(NStatus status) {
	// this.status = status;
	// this.resource.addProperty(NProperty.status, status.getResource());
	// this.resource.getModel().add(status.getResource().getModel());
	// }

	public NLabel getDisease() {
		return disease;
	}

	public void setDisease(NLabel disease) {
		this.disease = disease;
		this.resource.addProperty(NProperty.label, disease.getResource());
		this.resource.getModel().add(disease.getResource().getModel());
	}

	private NUserType type;

	public NUserType getType() {
		return type;
	}

	public void setType(NUserType type) {
		this.type = type;
		this.resource.addProperty(RDF.type, type.getResource());
		this.resource.getModel().add(type.getResource().getModel());
	}

	private Profile profile;

	private NPerson father, mother, spouse;

	public NPerson getFather() {
		return father;
	}

	public void setFather(NPerson father) {
		this.father = father;
		if (father != null) {
			Resource rf = father.getResource();
			if (rf != null) {
				// this.resource.addProperty(NProperty.hasFather, rf);
				rf.addProperty(NProperty.parentOf, this.resource);
			}
		}
	}

	public NPerson getMother() {
		return mother;
	}

	public void setMother(NPerson mother) {
		this.mother = mother;
		if (mother != null) {
			Resource rf = mother.getResource();
			if (rf != null) {
				// this.resource.addProperty(NProperty.hasMother, rf);
				rf.addProperty(NProperty.parentOf, this.resource);
			}
		}
	}

	public NPerson getSpouse() {
		return spouse;
	}

	public void setSpouse(NPerson spouse) {
		this.spouse = spouse;
		if (spouse != null) {
			Resource rf = spouse.getResource();
			if (rf != null) {
				// this.resource.addProperty(NProperty.hasSpouse, rf);
				this.resource.addProperty(NProperty.spouseOf, rf);
			}
		}
	}

	public NPerson[] getChildren() {
		return children;
	}

	public void setChildren(NPerson[] children) {
		this.children = children;
		if (children != null && children.length > 0) {
			Resource rf = null;
			for (NPerson child : children)
				if (child.getResource() != null) {
					rf = child.getResource();
					if (rf != null) {
						rf.addProperty(NProperty.childOf, this.resource);
						// this.resource.addProperty(NProperty.hasChild, rf);
					}
				}
		}
	}

	public void setSibling(NPerson person) {
		Resource rf = this.getResource();
		if (rf != null) {
			rf.addProperty(NProperty.siblingOf, person.getResource());
		}
	}

	private NPerson[] children;

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

		if (profile == null)
			return;
		else {
			Resource res = ModelFactory.createDefaultModel().createResource();
			res.addLiteral(FOAF.givenname, profile.getFirstName())
					.addLiteral(FOAF.family_name, profile.getSurname())
					.addLiteral(FOAF.gender, profile.getGender())
					.addLiteral(FOAF.birthday, profile.getBirthday())
					.addLiteral(
							NProperty.age,
							model.createTypedLiteral(Integer.valueOf(profile
									.getAge())))
					// .addLiteral(NProperty.address, profile.getAddress())
					// .addLiteral(NProperty.zipCode, profile.getZipCode())
					.addLiteral(FOAF.mbox, profile.getEmail());

			if (profile.getPhone() != null)
				res.addLiteral(FOAF.phone, profile.getPhone());

			this.resource.addProperty(NProperty.profile, res);
			this.resource.getModel().add(res.getModel());

			res.getModel().close();
		}
	}

	private Model model = NDataUtils.createModel();

	public Resource getResource() {

		// call to setup resource
		// this.getFather();
		// this.getMother();
		// this.getSpouse();
		// this.getChildren();

		return this.resource;
	}

	@Override
	public String toString() {
		if (this.getProfile() == null)
			return "profile is null";
		return NDataUtils.GSON.toJson(this.getProfile());
	}
}
