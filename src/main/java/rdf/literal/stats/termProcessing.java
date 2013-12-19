package rdf.literal.stats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import java.util.Set;

import rdf.literal.stats.test.MapUtil;

public class termProcessing {

	ArrayList<String> literalsListDuplicates;
	Set<String> literalsListNODuplicates;
	Map<String, Integer> wordMap;
	ArrayList<String> wordList;
	int threshold = 10000000;
	String termsFile = "result/terms.txt";
	private Boolean wasDumped = false;
	Boolean isTermMapConstructed = false;

	public Boolean getWasDumped() {
		return wasDumped;
	}

	public void setWasDumped(Boolean wasDumped) {
		this.wasDumped = wasDumped;
	}

	public termProcessing(ArrayList<String> literalsListDuplicates) {

		// literal are all loaded into memory
		this.literalsListDuplicates = literalsListDuplicates;
		termMapConstruction();
		printWordHistogram();
	}

	public termProcessing(String literalPath) {
		wordMap = new HashMap<String, Integer>();
		File in = new File(literalPath);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split("\\s+");
				for (String string2 : temp) {
					if (wordMap.containsKey(string2)) {
						Integer counter = wordMap.get(string2);
						wordMap.put(string2, counter + 1);
					} else {
						wordMap.put(string2, 1);
					}

				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void termMapConstruction() {
		wordMap = new HashMap<String, Integer>();
		for (String string : literalsListDuplicates) {
			String[] temp = string.split("\\s+");
			for (String string2 : temp) {
				if (wordMap.containsKey(string2)) {
					Integer counter = wordMap.get(string2);
					wordMap.put(string2, counter + 1);
				} else {
					wordMap.put(string2, 1);
				}
			}
		}
		isTermMapConstructed = true;
	}

	public void printWordHistogram() {
		if (isTermMapConstructed) {
			wordMap = MapUtil.sortByValue(wordMap);

			// /
			Map<Integer, Integer> histogrsmMap = histogramCal(wordMap);
			Map<Integer, Integer> sortedHistogramMap = new TreeMap<Integer, Integer>(
					histogrsmMap);
			Iterator<Integer> iterator3 = sortedHistogramMap.keySet()
					.iterator();
			while (iterator3.hasNext()) {
				Integer key = iterator3.next();
				Integer value = sortedHistogramMap.get(key);
				System.out.println(String.format("%-15s\t %s", key, value));
			}
		} else {
			System.out.println("termMap hasn't been constructed!!");
			System.exit(0);

		}
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

	private void wordSeperatorWithDuplicates2() {

		wordList = new ArrayList<String>();
		int i = 0; // counter of when to dump list to disk
		for (String string : literalsListDuplicates) {
			String[] temp = string.split("\\s+");
			for (String string2 : temp) {
				wordList.add(string2);
				i++;
				if (i > threshold) {
					setWasDumped(true);
					dumbToDisk();
					i = 0;
					wordList = new ArrayList<String>();
				}
			}
		}

	}

	private void wordSeperatorWithDuplicates2FromFile(String literalPath) {
		wordList = new ArrayList<String>();
		File in = new File(literalPath);
		try {
			int i = 0; // counter of when to dump list to disk
			BufferedReader reader = new BufferedReader(new FileReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] temp = line.split("\\s+");
				for (String string2 : temp) {
					wordList.add(string2);
					i++;
					if (i > threshold) {
						setWasDumped(true);
						dumbToDisk();
						i = 0;
						wordList = new ArrayList<String>();
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void dumbToDisk() {
		try {
			FileWriter fw = new FileWriter(termsFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < wordList.size(); i++) {
				bw.write(wordList.get(i));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void printWordCount() {
		System.out.println("Number of words with duplicates: "
				+ wordList.size());
	}

}
