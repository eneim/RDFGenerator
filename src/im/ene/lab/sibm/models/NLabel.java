package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.NDataUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class NLabel {

	private static final String BASE_LABEL = "http://lab.ene.im/SIBM/thing/label/";

	private String name;

	private Model model = NDataUtils.createModel();

	private Resource resource;

	public NLabel(String name) {
		this.name = name;

		resource = model.createResource(BASE_LABEL + name);
		resource.addLiteral(NProperty.labelName, name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Resource getResource() {
		return this.resource;
	}

	@Override
	public String toString() {
		return this.name;
	}

	private static final NLabel disease1 = new NLabel("Disease1");

	private static final NLabel disease2 = new NLabel("Disease2");

	private static final NLabel disease3 = new NLabel("Disease3");

	private static final NLabel disease4 = new NLabel("Disease4");

	private static final NLabel disease5 = new NLabel("Disease5");

	private static final NLabel injured1 = new NLabel("Injured1");

	private static final NLabel injured2 = new NLabel("Injured2");

	private static final NLabel injured3 = new NLabel("Injured3");

	public static final NLabel[] DISEASES = { disease1, disease2, disease3,
			disease4, disease5, injured1, injured2, injured3 };

}
