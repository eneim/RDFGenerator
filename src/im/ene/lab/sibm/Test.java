package im.ene.lab.sibm;

import im.ene.lab.sibm.models.NProperty;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.VCARD;

public class Test {

	public static void main(String[] args) {

		OntModel ontModel = ModelFactory.createOntologyModel();
		ontModel.setNsPrefix("sibm", "http://lab.ene.im/SIBM/owl/sibm.owl#");

		OntClass nPerson = ontModel.createClass(ontModel.getNsPrefixURI("sibm")
				+ "Person");
		OntClass nAddress = ontModel.createClass(ontModel
				.getNsPrefixURI("sibm") + "Address");

		Individual p1 = nPerson
				.createIndividual("http://lab.ene.im/SIBM/thing/person/person1");
		p1.addProperty(VCARD.BDAY, "Aug 5th, 1989").addProperty(VCARD.NAME,
				"Nam Nguyen");

		Individual add1 = nAddress
				.createIndividual("http://lab.ene.im/SIBM/thing/place/address1");
		add1.addProperty(VCARD.NAME, "Tokyo").addProperty(VCARD.CATEGORIES,
				"Capital");

		p1.addProperty(VCARD.ADR, add1);
		// p1.getModel().write(System.out, "Turtle");

		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("sibm", "http://lab.ene.im/SIBM/owl/sibm.owl#");
		model.setNsPrefix("gml", "http://www.opengis.net/gml/3.2#");

		Resource rP1 = model
				.createResource("http://lab.ene.im/SIBM/thing/person/person1")
				.addLiteral(VCARD.BDAY, "Aug 5th, 1989")
				.addLiteral(VCARD.NAME, "Nam Nguyen");

		Resource rAdd1 = model
				.createResource("http://lab.ene.im/SIBM/thing/place/address1")
				.addLiteral(VCARD.NAME, "Tokyo")
				.addLiteral(VCARD.CATEGORIES, "Capital");

		rP1.addProperty(NProperty.geopoint, rAdd1);
		rP1.getModel().write(System.out, "Turtle");

	}

}
