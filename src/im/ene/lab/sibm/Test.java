package im.ene.lab.sibm;


public class Test {

	public static void main(String[] args) throws Exception {

		int max = 10000;
		int i = 1;

		if (args[0] != null)
			max = Integer.valueOf(args[0]);
		
		NFileUtils bench = new NFileUtils("bench.txt");
		
		// warmup
		SIBM.benchmark(1);
		
		bench.start(true);
		while (i < max) {
			bench.writeLine(SIBM.benchmark(i));
			i *= 2;
		}
		
		bench.end();
	}

}
