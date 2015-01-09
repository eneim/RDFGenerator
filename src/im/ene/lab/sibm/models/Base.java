package im.ene.lab.sibm.models;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class Base {

	protected Model model;

	protected Resource resource;

	public Base(String name) {
		if (model == null)
			this.model = ModelFactory.createDefaultModel();

	}

	void initResource(String name) {

	}

	public Resource getResource() {
		return this.resource;
	}
}
