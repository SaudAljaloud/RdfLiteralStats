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
		JenaTesting ts = new JenaTesting();
		ts.openData(args);
		ts.queryResultSet();
		ts.printQueryResult();
		ts.countLiteralSpace();
		ts.wordSeperatorWithDuplicates();
		ts.wordSeperatorNoDuplicatesMap();

		// ts.openBigData(args);
		// System.out.println("loading litera file!!");
		// ts.readLiteralsFromFile(ts.literalsFile);
		// ts.printQueryResult();
		// ts.countLiteralSpace();
		// ts.wordSeperatorWithDuplicates();
		// ts.wordSeperatorNoDuplicates();
	}

	public void openData(String[] dataDir) {

		model = ModelFactory.createDefaultModel();
		if (dataDir.length > 0) {
			File dir = new File(dataDir[0]);
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.toString().contains("University")) {
					System.out.println(file.getAbsolutePath());
					model.read(file.getAbsolutePath());
				}
			}
		} else {
			model.read("/Users/saudaljaloud/RDFLiteralStats/ms_7.4_1.rdf");
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
				if (file.toString().contains("University")) {
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
					wordlist.put(string2, counter+=1);
				} else {
					wordlist.put(string2, 1);
				}
			}
		}
		
		wordlist = MapUtil.sortByValue( wordlist );
		

		Iterator iterator = wordlist.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			String value = wordlist.get(key).toString();
			System.out.println(String.format("%-15s==> %s" , key, value));
		}

	}

	public void countLiteralSpace() {
		int literalSpaceCounter = 0;
		for (String string : literalsListDuplicates) {
			literalSpaceCounter += string.length();
		}
		System.out.println("The total space of literal is: "
				+ literalSpaceCounter);
	}

	

}
