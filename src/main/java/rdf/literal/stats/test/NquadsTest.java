package rdf.literal.stats.test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class NquadsTest {

	Model model = ModelFactory.createDefaultModel();

	String query = "select * from <http://en.wikipedia.org/wiki/CAT:Professional_wrestling#> where { graph ?g { ?s ?p ?o .  }} limit 10";

	public static void main(String[] args) {
		NquadsTest ts = new NquadsTest();
		ts.doit();
	}

	public void doit() {
		
		model = ModelFactory.createDefaultModel();
		model.read("category_labels_en.nq");
		if (model.READ){
			System.out.println("right!!");
		}
		Query q = QueryFactory.create(query);
		QueryExecution qe = QueryExecutionFactory.create(q, model);
		ResultSet rs = qe.execSelect();
		ResultSetFormatter.out(rs);
		
		

	}

}
