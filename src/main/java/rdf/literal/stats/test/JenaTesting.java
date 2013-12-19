package rdf.literal.stats.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;

public class JenaTesting {

	/**
	 * @param args
	 */

	ArrayList<String> literalsListDuplicates = new ArrayList<String>();
	Set<String> literalsListNODuplicates = new HashSet<String>();
	Model model = null;
	final String query = "SELECT ?o\n" + "WHERE { ?s ?p ?o .\n"
			+ "FILTER isLiteral(?o)}";

	String resultDir = "result";
	String literalsFile = resultDir + "/Literals.txt";

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		
		JenaTesting ts = new JenaTesting();
//		ts.openData(args);
//		ts.queryResultSet();
		ts.runRiot();
		ts.printQueryResult();
		ts.countLiteralSpace();
		ts.wordSeperatorWithDuplicates();
		ts.wordSeperatorNoDuplicates();
		ts.wordSeperatorNoDuplicatesMap();

		// ts.openBigData(args);
		// System.out.println("loading litera file!!");
		// ts.readLiteralsFromFile(ts.literalsFile);
		// ts.printQueryResult();
		// ts.countLiteralSpace();
		// ts.wordSeperatorWithDuplicates();
		// ts.wordSeperatorNoDuplicates();
		
		long endTime = System.currentTimeMillis();
		System.out.println("That took " + (endTime - startTime) / 1000
				+ " milliseconds");
	}

	public void openData(String[] dataDir) {

		model = ModelFactory.createDefaultModel();
		if (dataDir.length > 0) {
			File dir = new File(dataDir[0]);
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.toString().contains("rdf")) {
					System.out.println(file.getAbsolutePath());
					try {
						model.read(file.getAbsolutePath(), "nquads");
					} catch (RiotException e) {
						System.out.println("File will be skipped: "
								+ file.getAbsolutePath());
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("error");
					}
				}
			}
		} else {
			model.read("category_labels_en.ttl");
			System.out.println("file loaded");
		}
	}

	int threshold = 50;
	int i = 0;

	public void openBigData(String[] dataDir) {

		model = ModelFactory.createDefaultModel();
		if (dataDir.length > 0) {
			File dir = new File(dataDir[0]);
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.toString().contains("rdf")) {
					System.out.println(file.getAbsolutePath());
					model.read(file.getAbsolutePath());
					i++;
					if (i > threshold) {
						queryResultSetToFile();
					}
				}
			}
		} else {
			model.read("/Users/saudaljaloud/RDFLiteralStats/ms_7.4_1.rdf");
		}
	}

	public void queryResultSetToFile() {

		try {
			FileWriter fw = new FileWriter(literalsFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			Query q = QueryFactory.create(query);
			QueryExecution qe = QueryExecutionFactory.create(q, model);
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
				QuerySolution soln = rs.nextSolution();
				// RDFNode x = soln.get("?o") ; // Get a result variable by
				// name.
				// Resource r = soln.getResource("?o"); // Get a result variable
				// -
				// must be a resource
				Literal l = soln.getLiteral("?o"); // Get a result variable -
													// must
													// be a literal
				bw.write(l.getLexicalForm());
				bw.newLine();

			}
			fw.close();
			i = 0; // reset file counter
			model.removeAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void readLiteralsFromFile(String filePath) {
		literalsListDuplicates = new ArrayList<String>();
		literalsListNODuplicates = new HashSet<String>();

		File in = new File(filePath);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// literalsListDuplicates.add(line);
				literalsListNODuplicates.add(line);

			}
			reader.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void runRiot() {
		final String filename = "article_categories_en.ttl";

		// Create a PipedRDFStream to accept input and a PipedRDFIterator to
		// consume it
		// You can optionally supply a buffer size here for the
		// PipedRDFIterator, see the documentation for details about recommended
		// buffer sizes

		PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
		
		final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

		// PipedRDFStream and PipedRDFIterator need to be on different threads
		ExecutorService executor = Executors.newSingleThreadExecutor();

		// Create a runnable for our parser thread
		Runnable parser = new Runnable() {

			public void run() {
				// Call the parsing process.
				RDFDataMgr.parse(inputStream, filename);
			}
		};

		// Start the parser on another thread
		executor.submit(parser);

		// We will consume the input on the main thread here

		// We can now iterate over data as it is parsed, parsing only runs as
		// far ahead of our consumption as the buffer size allows
		while (iter.hasNext()) {
			Triple next = iter.next();
			if (next.getObject().isLiteral()) {
				String literalLoxicalForm = next.getObject().getLiteralLexicalForm();
				literalsListDuplicates.add(literalLoxicalForm);
				literalsListNODuplicates.add(literalLoxicalForm);
			}
		}
	}

	public void queryResultSet() {

		Query q = QueryFactory.create(query);
		QueryExecution qe = QueryExecutionFactory.create(q, model);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			QuerySolution soln = rs.nextSolution();
			// RDFNode x = soln.get("?o") ; // Get a result variable by name.
			// Resource r = soln.getResource("?o"); // Get a result variable -
			// must be a resource
			Literal l = soln.getLiteral("?o"); // Get a result variable - must
												// be a literal
			literalsListDuplicates.add(l.getLexicalForm());
			literalsListNODuplicates.add(l.getLexicalForm());
		}
		// NodeIterator itr = model.listObjects();
		// while (itr.hasNext()) {
		// System.out.println(itr.next());
		// }

	}

	public void queryWalker() {
		Query q = QueryFactory.create(query);

		// This will walk through all parts of the query
		ElementWalker.walk(q.getQueryPattern(),
		// For each element...
				new ElementVisitorBase() {
					// ...when it's a block of triples...
					public void visit(ElementPathBlock el) {
						// ...go through all the triples...
						Iterator<TriplePath> triples = el.patternElts();
						while (triples.hasNext()) {
							// ...and grab the subject
							// if (triples.next().getObject().isLiteral()) {
							// literalsList.add(triples.next().getObject()
							// .getLiteralLexicalForm());
							// }
							System.out.println(triples.next().getSubject());
						}
					}
				});

	}

	public void printQueryResult() {
		System.out.println("Number of Literal objects with duplicates: "
				+ literalsListDuplicates.size());
		System.out.println("Number of Literal objects NO duplicates: "
				+ literalsListNODuplicates.size());
		// for (String string : literalsListDuplicates) {
		// System.out.println(string);
		// }
	}

	public void wordSeperatorWithDuplicates() {

		ArrayList<String> wordList = new ArrayList<String>();
		for (String string : literalsListDuplicates) {
			String[] temp = string.split("\\s+");
			for (String string2 : temp) {
				wordList.add(string2);
			}
		}
		
		// for (String string : wordList) {
		// System.out.println(string);
		// }

		System.out.println("Number of words with duplicates: "
				+ wordList.size());

	}

	public void wordSeperatorNoDuplicates() {
		Set<String> wordList = new HashSet<String>();
		for (String string : literalsListNODuplicates) {
			String[] temp = string.split("\\s+");
			for (String string2 : temp) {
				wordList.add(string2);
			}
		}

		// for (String string : wordList) {
		// System.out.println(string);
		// }
		System.out.println("Number of words NO duplicates: " + wordList.size());

	}

	public void wordSeperatorNoDuplicatesMap() {

		Map<String, Integer> wordlist = new HashMap<String, Integer>();
		for (String string : literalsListDuplicates) {
			String[] temp = string.split("\\s+");
			for (String string2 : temp) {
				if (wordlist.containsKey(string2)) {
					Integer counter = wordlist.get(string2);
					wordlist.put(string2, counter + 1);
				} else {
					wordlist.put(string2, 1);
				}
			}
		}

		wordlist = MapUtil.sortByValue(wordlist);

		// /
		Map<Integer, Integer> histogrsmMap = histogramCal(wordlist);
		Map<Integer, Integer> sortedHistogramMap = new TreeMap<Integer, Integer>(
				histogrsmMap);
		Iterator<Integer> iterator3 = sortedHistogramMap.keySet().iterator();
		while (iterator3.hasNext()) {
			Integer key = iterator3.next();
			Integer value = sortedHistogramMap.get(key);
			System.out.println(String.format("%-15s\t %s", key, value));
		}

		// // to file
		// Iterator iterator2 = wordlist.keySet().iterator();
		// try {
		// FileWriter fw = new FileWriter("result/WordOccuranceMap.txt", true);
		// BufferedWriter bw = new BufferedWriter(fw);
		// while (iterator2.hasNext()) {
		// String key = iterator2.next().toString();
		// String value = wordlist.get(key).toString();
		// bw.write(String.format("%-15s\t %s", key, value));
		// bw.newLine();
		// }
		// bw.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	public void countLiteralSpace() {
		int literalSpaceCounter = 0;
		for (String string : literalsListDuplicates) {
			literalSpaceCounter += string.length();
		}
		System.out.println("The total space of literal is: "
				+ literalSpaceCounter);
	}

	
	public Map<Integer, Integer> histogramCal(Map<String, Integer> wordlist) {

		Map<Integer, Integer> histogram = new HashMap<Integer, Integer>();
		Iterator<String> iterator = wordlist.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Integer value = wordlist.get(key);

			int[] ss = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000,
					100000, 1000000, 10000000 };
			for (int i = 0; i < ss.length; i++) {
				if (ss[i] >= value || ss[i + 1] > value) {
					if (histogram.containsKey(ss[i])) {
						Integer counter = histogram.get(ss[i]);
						histogram.put(ss[i], counter + 1);
						break;
					} else {
						histogram.put(ss[i], 1);
						break;
					}
				}
			}

		}

		return histogram;
	}

}
