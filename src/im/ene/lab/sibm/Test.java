package im.ene.lab.sibm;

import im.ene.lab.sibm.generator.Generator;
import im.ene.lab.sibm.models.NPerson;


public class Test {

	public static void main(String[] args) throws Exception {

		NPerson[] family = Generator.genFamily("Fname", 2);
		
		for (NPerson p : family) {
			System.out.println(p.toString());
		}
	}

}
