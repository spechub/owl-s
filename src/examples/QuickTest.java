package examples;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;

import org.mindswap.owl.OWLClass;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.service.Service;

/**
 * This class is intended for the developers of the API to perform quick tests.
 * Do use this class for whatever you intend to, but do never ask for support :-)
 *  
 * @author Michael Dänzer, University of Zurich
 * @date 16.01.2007
 */
public class QuickTest {
	private OWLKnowledgeBase kb;
	
	public static void main(String[] args) {
		QuickTest test = new QuickTest();
		test.languageTest();
	}

	public QuickTest() {
		kb = OWLFactory.createKB();
		kb.setReasoner("Pellet");		
	}
	
	private void languageTest() {
		kb.getReader().getCache().setLocalCacheDirectory("E://Workspaces//NExT//Ontologies//ont_cache");
		kb.getReader().getCache().setForced(true);
		
		Service service;
		try {
			service = kb.readService("http://www.ifi.unizh.ch/ddis/ont/next/kb/ProcessLibrary/AtomicProcesses/Add.owl");
			
			List classes = kb.getNonLanguageClasses();
			List dataProps = kb.getNonLanguageDataProperties();
			List objProps = kb.getNonLanguageObjectProperties();
			for (int i = 0; i < classes.size(); i++)
				System.out.println(((OWLClass) classes.get(i)).getURI());
			System.out.println("--------------------------------------");
			for (int i = 0; i < dataProps.size(); i++)
				System.out.println(dataProps.get(i));
			System.out.println("--------------------------------------");
			for (int i = 0; i < objProps.size(); i++)
				System.out.println(objProps.get(i));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private void pelletErrorTest() {
		kb.getReader().getCache().setLocalCacheDirectory("E://Workspaces//NExT//Ontologies//ont_cache");
		kb.getReader().getCache().setForced(true);
		
		Service service;
		try {
			service = kb.readService("http://www.ifi.unizh.ch/ddis/ont/next/ProcessSpace/Projectgigimgii.owl#gigimgii");
			service.getOntology().write(System.out);
			service.deleteProcess();
			service.getOntology().write(System.out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
