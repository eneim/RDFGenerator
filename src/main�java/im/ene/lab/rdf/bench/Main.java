package im.ene.lab.rdf.bench;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class Main {

	public static void main(String[] args) throws IOException {
		// String dir = "data/dataset";
		// File dbDir = new File(dir);
		// if (!dbDir.exists())
		// dbDir.mkdirs();
		//
		int mb = 1024 * 1024;
		long mem;

		// Dataset dataset = TDBFactory.createDataset(dir);
		// Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();
		// Print free memory
		mem = runtime.freeMemory();
		System.out.println("Free Memory before:" + mem / mb);

		String source = "src/im/ene/lab/rdf/bench/u0.owl";

		File outFile = new File("src/im/ene/lab/rdf/bench/u0.txt");
		FileWriter outFileWriter = new FileWriter(outFile, false);

		// dataset.begin(ReadWrite.WRITE);
		// Model model = dataset.getNamedModel("P20-12_13");

		OntModel model = ModelFactory.createOntologyModel();
		FileManager.get().readModel(model, source);
		model.write(outFileWriter, "Turtle");

		long memAfter = runtime.freeMemory();
		// Print free memory
		System.out.println("Free Memory after:" + memAfter / mb);
		System.out.println("Used mem:" + (mem - memAfter) / mb);
		// dataset.commit();
		// dataset.end();
	}

}
