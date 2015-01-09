package im.ene.lab.sibm;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.VCARD;

public class Test {

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();

		model.setNsPrefix("sibm", "http://lab.ene.im/SIBM/owl/sibm.owl#");
		model.createResource("http://lab.ene.im/SIBM/thing/person/person1")
				.addLiteral(VCARD.BDAY, "Aug 5th, 1989")
				.addLiteral(VCARD.NAME, "Nam Nguyen");

		// model.write(System.out, "Turtle");

		OntModel ontModel = ModelFactory.createOntologyModel();
		String sibm = "http://lab.ene.im/SIBM/owl/sibm.owl#";
		
		ontModel.setNsPrefix("sibm", sibm);
		OntClass foo = ontModel.createClass(sibm + "Person");
		Individual fubar = foo.createIndividual(sibm + "fubar");
		fubar.addLiteral(VCARD.NAME, "Nam Nguyen");

		ontModel.write(System.out, "Turtle");
	}
}
