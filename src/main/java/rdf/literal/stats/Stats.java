package rdf.literal.stats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Stats {

	ArrayList<String> literalsListDuplicates;
	Set<String> literalsListNODuplicates;

	public Stats(ArrayList<String> literalsListDuplicates,
			Set<String> literalsListNODuplicates) {

		this.literalsListDuplicates = literalsListDuplicates;
		this.literalsListNODuplicates = literalsListNODuplicates;
	}

	public void printQueryResult() {
		System.out.println("Number of Literal objects with duplicates: "
				+ literalsListDuplicates.size());
		System.out.println("Number of Literal objects NO duplicates: "
				+ literalsListNODuplicates.size());

	}

}
