package im.ene.lab.rdf.bench;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class Main {

	public static void main(String[] args) {
		// String dir = "data/dataset";
		// File dbDir = new File(dir);
		// if (!dbDir.exists())
		// dbDir.mkdirs();
		//
		// Dataset dataset = TDBFactory.createDataset(dir);

		String source = "src/im/ene/lab/rdf/bench/sample_data.rdf";

		// dataset.begin(ReadWrite.WRITE);
		// Model model = dataset.getNamedModel("P20-12_13");

		OntModel model = ModelFactory.createOntologyModel();
		FileManager.get().readModel(model, source);
		model.write(System.out, "RDF/JSON");
		// dataset.commit();
		// dataset.end();
	}

}
