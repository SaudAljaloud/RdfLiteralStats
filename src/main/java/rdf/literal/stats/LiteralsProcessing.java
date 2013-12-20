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

import com.hp.hpl.jena.graph.Triple;

public class LiteralsProcessing {

	int tripleCounter = 0;

	final int threshold = 1000000;
	final String literalsFile = "result/Literals.txt";

	private ArrayList<String> literalsListDuplicates = new ArrayList<String>();

	public ArrayList<String> getLiteralsListDuplicates() {
		return literalsListDuplicates;
	}

	public void setLiteralsListDuplicates(
			ArrayList<String> literalsListDuplicates) {
		this.literalsListDuplicates = literalsListDuplicates;
	}

	private Set<String> literalsListNODuplicates = new HashSet<String>();

	public Set<String> getLiteralsListNODuplicates() {
		return literalsListNODuplicates;
	}

	public void setLiteralsListNODuplicates(Set<String> literalsListNODuplicates) {
		this.literalsListNODuplicates = literalsListNODuplicates;
	}

	private Boolean wasDumped = false;

	public Boolean getWasDumped() {
		return wasDumped;
	}

	public void setWasDumped(Boolean wasDumped) {
		this.wasDumped = wasDumped;
	}

	public void extractLiteralFromFile(final String filePath) {
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
			tripleCounter++;
			if (next.getObject().isLiteral()) {
				System.out.println(next.getObject().getLiteralDatatype());
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

	public void getLiteralFromFile() {
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

	public void printLiteralCount() {
		System.out.println("Number of Triples: " + tripleCounter);
		System.out.println("Number of Literal objects with duplicates: "
				+ getLiteralsListDuplicates().size());
		System.out.println("Number of Literal objects NO duplicates: "
				+ getLiteralsListNODuplicates().size());

	}

	public void printLiterlAverageLength() {
		int length = 0;
		for (int i = 0; i < getLiteralsListDuplicates().size(); i++) {
			length = length + getLiteralsListDuplicates().get(i).length();

		}

		System.out.println("The average length of literals: " + length
				/ getLiteralsListDuplicates().size());
	}
}
