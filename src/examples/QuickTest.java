package examples;

import java.util.Iterator;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.generic.list.OWLSObjList;
import org.mindswap.owls.process.AnyOrder;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstructBag;
import org.mindswap.owls.process.ControlConstructList;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Sequence;
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
	String uri = "http://example.com/";
	private OWLKnowledgeBase kb;
	private Perform p1;
	private Perform p2;
	private Perform p3;
	private Perform p4;
	
	public static void main(String[] args) {
		QuickTest test = new QuickTest();
		test.removeCCTest();
	}

	public QuickTest() {
		kb = OWLFactory.createKB();
		kb.setReasoner("Pellet");		
	}
	
	private void removeCCTest() {
		AnyOrder anyorder = kb.createAnyOrder(URIUtils.createURI(uri + "anyOrder"));
		createList();
		anyorder.addComponent(p4);
		anyorder.addComponent(p3);
		anyorder.addComponent(p2);
		anyorder.addComponent(p1);			
		
		printSizeAndMembers(anyorder.getComponents());
		
		anyorder.removeConstruct(p4);
		anyorder.removeConstruct(p3);
		anyorder.removeConstruct(p2);
		anyorder.removeConstruct(p1);
		
		printSizeAndMembers(anyorder.getComponents());
	}	
	
	private ControlConstructBag createList() {
		String uri = "http://example.com/p";
		
		p1 = kb.createPerform(URIUtils.createURI(uri + "1"));
		ControlConstructBag list = kb.createControlConstructBag(p1);
		
		p2 = kb.createPerform(URIUtils.createURI(uri + "2"));
		list = (ControlConstructBag) list.insert(p2);
		
		p3 = kb.createPerform(URIUtils.createURI(uri + "3"));
		list = (ControlConstructBag) list.insert(p3);
		
		p4 = kb.createPerform(URIUtils.createURI(uri + "4"));
		list = (ControlConstructBag) list.insert(p4);
		
		return list;
	}
	
	private void printSizeAndMembers(OWLSObjList list) {
		System.out.println("List size " + list.size());
		System.out.println("List members:");
		Iterator iter = list.iterator();
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
