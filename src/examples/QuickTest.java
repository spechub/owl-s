package examples;

import impl.owl.list.RDFListImpl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.list.RDFList;
import org.mindswap.owl.vocabulary.RDF;
import org.mindswap.owls.generic.list.OWLSObjList;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstructList;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.utils.URIUtils;

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
		test.removeListTest();
	}

	private void removeListTest() {
		OWLKnowledgeBase kb = OWLFactory.createKB();
		kb.setReasoner("Pellet");
		String uri = "http://example.com/p";
		
		Perform p1 = kb.createPerform(URIUtils.createURI(uri + "1"));
		OWLSObjList list = kb.createControlConstructList(p1);
		
		Perform p2 = kb.createPerform(URIUtils.createURI(uri + "2"));
		list = (OWLSObjList) list.insert(p2);
		
		Perform p3 = kb.createPerform(URIUtils.createURI(uri + "3"));
		list = (OWLSObjList) list.insert(p3);
		
		Perform p4 = kb.createPerform(URIUtils.createURI(uri + "4"));
		list = (OWLSObjList) list.insert(p4);
				
		System.out.println("List size " + list.size());
		System.out.println("List members:");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			System.out.println("\t" + iter.next().toString());
		}
		
		RDFList rdflist = list.remove();		
		System.out.println("List size " + rdflist.size());
		System.out.println("List members:");
		iter = rdflist.iterator();
		while (iter.hasNext()) {
			System.out.println("\t" + iter.next().toString());
		}
	}
	
	
	
	private void removeTest() {
		OWLKnowledgeBase kb = OWLFactory.createKB();
		kb.setReasoner("Pellet");
		
		Service service = kb.createService(URIUtils.createURI("http://examples.org#service"));
		CompositeProcess process = kb.createCompositeProcess(URIUtils.createURI("http://examples.org#one"));
		Perform perform1 = kb.createPerform(URIUtils.createURI("http://examples.org#perform1"));
		Perform perform2 = kb.createPerform(URIUtils.createURI("http://examples.org#perform2"));
		Sequence sequence = kb.createSequence(URIUtils.createURI("http://examples.org#sequence"));
		sequence.addComponent(perform1);
		sequence.addComponent(perform2);
		process.setComposedOf(sequence);
		process.setLabel("TestLabel");
		service.setProcess(process);
		
		kb.write(System.out);	
		System.out.println("--------------------");
		
		process.deleteComposedOf();
		service.deleteProcess();
		
		
		kb.write(System.out);		
	}
}
