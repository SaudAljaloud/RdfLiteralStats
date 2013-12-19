package rdf.literal.stats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.slf4j.Logger;

import com.hp.hpl.jena.graph.Triple;

public class Main {

	/**
	 * @param args
	 */

	Logger log = org.slf4j.LoggerFactory.getLogger(Main.class);
	ArrayList<String> literalsListDuplicates = new ArrayList<String>();
	Set<String> literalsListNODuplicates = new HashSet<String>();
	int threshold = 1000000;
	String literalsFile = "result/Literals.txt";
	private Boolean wasDumped = false;

	public Boolean getWasDumped() {
		return wasDumped;
	}

	public void setWasDumped(Boolean wasDumped) {
		this.wasDumped = wasDumped;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main main = new Main();

		if (args.length == 0) {
			System.out.println("insert arg!!");
			System.exit(0);
		} else if (args.length == 1) {
			if (new File(args[0]).isFile()) {
				main.forEachFile(args[0]);
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
				main.rdfFromDir(args[0], args[1]);
			}
		} else {
			System.out.println("type --help");
			System.exit(0);
		}

		if (main.getWasDumped()) {
			main.getLiteralFromFile();
		}
		Stats st = new Stats(main.literalsListDuplicates, main.literalsListNODuplicates);
		st.printQueryResult();

		
		termProcessing tm = new termProcessing(main.literalsListDuplicates);
		

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

					forEachFile(file.getAbsolutePath());
				}
			}
		}

	}

	public void forEachFile(final String filePath) {

		PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();

		final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

		// PipedRDFStream and PipedRDFIterator need to be on different threads
		ExecutorService executor = Executors.newSingleThreadExecutor();

		// Create a runnable for our parser thread
		Runnable parser = new Runnable() {

			public void run() {
				// Call the parsing process.
				RDFDataMgr.parse(inputStream, filePath);
			}
		};

		// Start the parser on another thread
		executor.submit(parser);

		// We will consume the input on the main thread here

		// We can now iterate over data as it is parsed, parsing only runs as
		// far ahead of our consumption as the buffer size allows

		int i = 0; // counter of when to dump list to disk
		while (iter.hasNext()) {
			Triple next = iter.next();
			if (next.getObject().isLiteral()) {
				String literalLoxicalForm = next.getObject()
						.getLiteralLexicalForm();
				literalsListDuplicates.add(literalLoxicalForm);
				literalsListNODuplicates.add(literalLoxicalForm);
				i++;
				if (i > threshold) {
					setWasDumped(true);
					dumbToDisk();
					i = 0;
					literalsListDuplicates = new ArrayList<String>();
					literalsListNODuplicates = new HashSet<String>();
				}
			}
		}

	}

	public void dumbToDisk() {
		try {
			FileWriter fw = new FileWriter(literalsFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < literalsListDuplicates.size(); i++) {
				bw.write(literalsListDuplicates.get(i));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getLiteralFromFile() {
		literalsListDuplicates = new ArrayList<String>();
		literalsListNODuplicates = new HashSet<String>();

		File in = new File(literalsFile);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				literalsListDuplicates.add(line);
				literalsListNODuplicates.add(line);

			}
			reader.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}