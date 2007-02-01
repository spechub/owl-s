package examples;

import impl.jena.OWLOntologyImpl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.utils.URIUtils;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class is intended for the developers of the API to perform quick tests.
 * Do use this class for whatever you intend to, but do never ask for support :-)
 *  
 * @author Michael Dänzer, University of Zurich
 * @date 16.01.2007
 */
public class QuickTest {
	public static void main(String[] args) {
		QuickTest test = new QuickTest();
		test.jenaTest();
		//test.deprecatedTest();
		//test.test();
		//test.testNow();
	}

	private void jenaTest() {
		OWLKnowledgeBase kb = OWLFactory.createKB();
		kb.setReasoner("Pellet");
		
		Service service = kb.createService(URIUtils.createURI("http://examples.org#service"));
		CompositeProcess process = kb.createCompositeProcess(URIUtils.createURI("http://examples.org#one"));
		service.setProcess(process);
		
		// get instances in service model
		OntModel modelService = ((OWLOntologyImpl) service.getOntology()).getOntModel();
		Resource resource = (Resource) process.getImplementation();
		
		StmtIterator stmtiter = modelService.listStatements(resource, (Property) null, (RDFNode) null);
		modelService.remove(stmtiter);
		
		stmtiter = modelService.listStatements((Resource) null, (Property) null, (RDFNode) resource);
		modelService.remove(stmtiter);
				
		kb.write(System.out);		
	}
	
	
	private void deprecatedTest() {
		OWLKnowledgeBase kb = OWLFactory.createKB();
		kb.setReasoner("Pellet");
		
		Service service = kb.createService(URIUtils.createURI("http://examples.org#service"));
		CompositeProcess process = kb.createCompositeProcess(URIUtils.createURI("http://examples.org#one"));
		service.setProcess(process);
		
		service.removeProcess();	
		process.removeTypes();
		
		kb.write(System.out);
		System.out.println("-------------------------------------------------------");
		System.out.println("-------------------------------------------------------");
		System.out.println("-------------------------------------------------------");
		System.out.println("-------------------------------------------------------");
		
		CompositeProcess process2 = kb.createCompositeProcess(URIUtils.createURI("http://examples.org#two"));
		service.setProcess(process2);
		kb.write(System.out);
	}
	
	private void test() {
		OWLKnowledgeBase kb = OWLFactory.createKB();
		kb.setReasoner("Pellet");

        // read the service description
        Service service;
		try {
			service = kb.readService("http://www.mindswap.org/2004/owl-s/1.1/FindCheaperBook.owl");
			Map map = service.getProperties();
			System.out.println(map);

			Map map2 = service.getType().getProperties();
			System.out.println(map2);
			
			List list = service.getProcess().getType().getDeclaredProperties();
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();			
		}              
	}
	
	private void testNow() {
		OWLKnowledgeBase kb = OWLFactory.createKB();

        // read the service description
        Service service;
		try {
			service = kb.readService("http://www.mindswap.org/2004/owl-s/1.1/FindCheaperBook.owl");
			Process process = service.getProcess();
			Profile profile = service.getProfile();
			Grounding ground = service.getGrounding();
			
			System.out.println(process.getLocalName());
			System.out.println(profile.getLocalName());
			System.out.println(ground.getLocalName());
			System.out.println("----------------------");
			
			service.removeProcess();
			service.removeProfile(profile);
			service.removeGrounding(ground);
			
			System.out.println(process.getLocalName());
			System.out.println(profile.getLocalName());
			System.out.println(ground.getLocalName());
			System.out.println("----------------------");
			
			System.out.println(service.toRDF());
			System.out.println("----------------------");
			
			service.setProcess(process);
			service.setProfile(profile);
			service.setGrounding(ground);
			
			System.out.println(process.getLocalName());
			System.out.println(profile.getLocalName());
			System.out.println(ground.getLocalName());
			System.out.println("----------------------");
			System.out.println(service.toRDF());
		} catch (Exception e) {
			e.printStackTrace();			
		}              
	}

}
