package rdf.literal.stats.test;

import java.util.Iterator;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFReaderF;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class NquadsTest {

	Model model = ModelFactory.createDefaultModel();

	String query = "SELECT * WHERE { ?s ?p ?o .\n" + 
			"}";

	public static void main(String[] args) {
		NquadsTest ts = new NquadsTest();
		ts.doit();
	}

	public void doit() {
		
		model = ModelFactory.createDefaultModel();
		RDFDataMgr.read(model, "category_labels_en.nq");
		if (model.READ){
			System.out.println("right!!");
		}
	
		
		StmtIterator iter = model.listStatements();
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			System.out.println(stmt.toString());
			RDFNode object = stmt.getObject();
			System.out.println(object);
		}
		
		
//		Query q = QueryFactory.create(query);
//		QueryExecution qe = QueryExecutionFactory.create(q, model);
//		ResultSet rs = qe.execSelect();
//		ResultSetFormatter.out(rs);
		
		

	}

}
