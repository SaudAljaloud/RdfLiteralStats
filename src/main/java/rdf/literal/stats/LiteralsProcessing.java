package rdf.literal.stats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.slf4j.Logger;

import com.hp.hpl.jena.graph.Triple;

/**
 * User: Saud Aljaloud email: sza1g10@ecs.soton.ac.uk
 */

public class LiteralsProcessing {
	Logger log = org.slf4j.LoggerFactory.getLogger(Main.class);

	int tripleCounter = 0;

	int plainLiteral = 0;
	int typedLiteral = 0;

	Map<String, Integer> otherLiteralTypesMap = new HashMap<String, Integer>();

	Map<String, Integer> prediateLiteralMap = new HashMap<String, Integer>();

	final int threshold = 100000000;
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
		log.debug("Start itrating over the file: " + filePath);
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

				// Starting calculate literal regarding each predicate
				String predicate = next.getPredicate().getURI();
				if (prediateLiteralMap.containsKey(predicate)) {
					Integer value = prediateLiteralMap.get(predicate);
					prediateLiteralMap.put(predicate, value + 1);
				} else {
					prediateLiteralMap.put(predicate, 1);
				}
				// Finishing calculate literal regarding each predicate

				if (next.getObject().getLiteralDatatype() == null) {
					plainLiteral++;
				} else {
					typedLiteral++;

					String temp = next.getObject().getLiteralDatatype()
							.getURI();
					if (otherLiteralTypesMap.containsKey(temp)) {
						Integer value = otherLiteralTypesMap.get(temp);
						otherLiteralTypesMap.put(temp, value + 1);
					} else {
						otherLiteralTypesMap.put(temp, 1);
					}

				}
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

		log.debug("Finish intrating over the file: " + filePath);
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
		System.out.println("Number of Literal objects with duplicates: "
				+ getLiteralsListDuplicates().size());
		System.out.println("Number of Literal objects NO duplicates: "
				+ getLiteralsListNODuplicates().size());

	}

	public void printLiteralRatioAgainstTriples() {
		System.out
				.println("Average literals against triples: "
						+ ((float) getLiteralsListDuplicates().size() / (float) tripleCounter));
	}

	public void printLiterlAverageLength() {
		int length = 0;
		for (int i = 0; i < getLiteralsListDuplicates().size(); i++) {
			length = length + getLiteralsListDuplicates().get(i).length();

		}

		System.out.println("The average length of literals: " + length
				/ getLiteralsListDuplicates().size());
	}

	public void printTriplesCount() {
		System.out.println("Triples Count: " + tripleCounter);

	}

	public void printliteralTypesStats() {
		System.out.println("Plain Literals: " + plainLiteral);
		System.out.println("Typed-Literals: " + typedLiteral);

	}

	public void printLiteralDataTypesURI() {
		otherLiteralTypesMap = MapUtil.sortByValue(otherLiteralTypesMap);
		Iterator<String> itr = otherLiteralTypesMap.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			Integer value = otherLiteralTypesMap.get(key);
			System.out.println(String.format("%-60s\t %s", key, value));
		}
	}

	public void printPrediateWithLiteralMap() {
		prediateLiteralMap = MapUtil.sortByValue(prediateLiteralMap);
		Iterator<String> itr = prediateLiteralMap.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			Integer value = prediateLiteralMap.get(key);
			System.out.println(String.format("%-60s\t %s", key, value));
		}
	}

	public void printStatsToFile() {
		try {
			FileWriter fw = new FileWriter("Stats.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("Triples Count: " + tripleCounter);
			bw.newLine();
			bw.write("Number of Literal objects with duplicates: "
					+ getLiteralsListDuplicates().size());
			bw.newLine();
			bw.write("Number of Literal objects NO duplicates: "
					+ getLiteralsListNODuplicates().size());
			bw.newLine();

			bw.write("Average literals against triples: "
					+ ((float) getLiteralsListDuplicates().size() / (float) tripleCounter));
			bw.newLine();

			prediateLiteralMap = MapUtil.sortByValue(prediateLiteralMap);
			Iterator<String> itr = prediateLiteralMap.keySet().iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				Integer value = prediateLiteralMap.get(key);
				bw.write(String.format("%-60s\t %s", key, value));
				bw.newLine();
			}

			bw.write("Plain Literals: " + plainLiteral);
			bw.newLine();
			bw.write("Typed-Literals: " + typedLiteral);
			bw.newLine();

			int length = 0;
			for (int i = 0; i < getLiteralsListDuplicates().size(); i++) {
				length = length + getLiteralsListDuplicates().get(i).length();

			}

			bw.write("The average length of literals: " + length
					/ getLiteralsListDuplicates().size());
			bw.newLine();

			otherLiteralTypesMap = MapUtil.sortByValue(otherLiteralTypesMap);
			Iterator<String> itr2 = otherLiteralTypesMap.keySet().iterator();
			while (itr2.hasNext()) {
				String key = itr2.next();
				Integer value = otherLiteralTypesMap.get(key);
				bw.write(String.format("%-60s\t %s", key, value));
				bw.newLine();
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
