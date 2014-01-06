package rdf.literal.stats;

import java.io.File;
import org.slf4j.Logger;

public class Main {

	/**
	 * @param args
	 */

	Logger log = org.slf4j.LoggerFactory.getLogger(Main.class);

	LiteralsProcessing lit = new LiteralsProcessing();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();
		main.argHandelling(args);
		System.exit(0);
	}

	public void argHandelling(String[] args) {
		if (args.length == 0) {
			System.out.println("insert arg!!");
			System.exit(0);
		} else if (args.length == 1) {
			if (new File(args[0]).isFile()) {
				lit.extractLiteralFromFile(args[0]);
			} else if (new File(args[0]).isDirectory()) {
				System.out
						.println("add another arg as a pattern for files to be included i.e .ttl");
				System.exit(0);
			} else {
				System.out.println("The arg is not of file-type");
				System.exit(0);
			}
		} else if (args.length == 2) {
			if (new File(args[0]).isDirectory()) {
				rdfFromDir(args[0], args[1]);
			}
		} else {
			System.out.println("type --help");
			System.exit(0);
		}

		if (lit.getWasDumped()) {
			lit.getLiteralFromFile();
		}
		lit.printTriplesCount();
		lit.printLiteralCount();
		lit.printLiteralRatioAgainstTriples();
		lit.printPrediateWithLiteralMap();
		lit.printliteralTypesStats();
		lit.printLiterlAverageLength();
		lit.printLiteralDataTypesURI();

		termProcessing tm = new termProcessing(lit.getLiteralsListDuplicates());
		tm.printWordCount();
		tm.printWordHistogram();
		tm.printTermMapToFile();

	}

	public void rdfFromDir(String path, String filePattern) {
		File folder = new File(path);
		if (folder.isDirectory()) {
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				File file = listOfFiles[i];
				if (file.isFile() && file.getName().contains(filePattern)) {
					log.debug("The file will be processed is: "
							+ file.getPath());

					lit.extractLiteralFromFile(file.getAbsolutePath());
				}
			}
		}

	}

}