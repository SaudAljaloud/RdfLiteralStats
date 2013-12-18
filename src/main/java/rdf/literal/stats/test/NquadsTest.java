package rdf.literal.stats.test;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;

public class NquadsTest {

	

	String query = "select * from <http://en.wikipedia.org/wiki/CAT:Futurama#> where { ?s ?p ?o .  } limit 10";

	public static void main(String[] args) {
		NquadsTest ts = new NquadsTest();
		ts.doit();
	}

	public void doit() {
		Dataset dataset = DatasetFactory.createMem();
		
		Model model = dataset.getDefaultModel();
		model.read("category_labels_en.nq");
		if (model.READ){
			System.out.println("right!!");
		}
		Query q = QueryFactory.create(query);
		QueryExecution qe = QueryExecutionFactory.create(q, model);
		ResultSet rs = qe.execSelect();
		ResultSetFormatter.out(rs);
		
		

	}
	
	public void tdbload() {
		
		Dataset dataset = TDBFactory.createDataset("tdb");
		Model model = dataset.getNamedModel("fdg");
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
