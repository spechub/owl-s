package examples;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mindswap.owl.OWLClass;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.grounding.JavaAtomicGrounding;
import org.mindswap.owls.grounding.WSDLAtomicGrounding;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;

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
		test.testImplementation();
	}

	public QuickTest() {
		kb = OWLFactory.createKB();
		kb.setReasoner("Pellet");		
	}
	
	private void testImplementation() {
		kb.getReader().getCache().setLocalCacheDirectory("E://Workspaces//NExT//Ontologies//ont_cache");
		kb.getReader().getCache().setForced(true);
		
		Service service = null;
		try {
			//service = kb.readService("http://www.mindswap.org/2004/owl-s/1.1/BabelFishTranslator.owl");
			//service = kb.readService("http://www.ifi.unizh.ch/ddis/ont/next/kb/ProcessLibrary/processes/Add.owl");
//			service = kb.readService("http://www.dfki.de/scallops/health-scallops/MedicalFlightCompanyServices.owl");
//			service = kb.readService("http://www.dfki.de/scallops/health-scallops/NonMedicalFlightCompanyServices.owl");
//			service = kb.readService("http://www.dfki.de/scallops/health-scallops/MedicalTransportCompanyServices.owl");
//			service = kb.readService("http://www.dfki.de/scallops/health-scallops/NonMedicalTransportCompanyServices.owl");
//			service = kb.readService("http://www.dfki.de/scallops/health-scallops/EMAServices.owl");
//			kb.read("http://www.dfki.de/scallops/health-scallops/EMAOntology.owl");
			kb.read("http://www.dfki.de/scallops/health-scallops/EMAOntology.owl");
			
			
			OWLIndividualList list = kb.getIndividuals();
			for (int i = 0; i < list.size(); i++) { 
				System.out.println("Types for " + list.individualAt(i) + " are ");
				Set types = list.individualAt(i).getTypes();
				Iterator iter = types.iterator();
				while (iter.hasNext())
					System.out.println("\t" + iter.next());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void removeTest() {
		kb.getReader().getCache().setLocalCacheDirectory("E://Workspaces//NExT//Ontologies//ont_cache");
		kb.getReader().getCache().setForced(true);
		
		Service service = null;
		try {
			service = kb.readService("http://www.mindswap.org/2004/owl-s/1.1/BabelFishTranslator.owl");
//			service = kb.readService("http://www.ifi.unizh.ch/ddis/ont/next/kb/ProcessLibrary/processes/Add.owl");
			
			Input in = service.getProcess().getInputs().inputAt(0);
			Output out = service.getProcess().getOutputs().outputAt(0);
			WSDLAtomicGrounding ground = (WSDLAtomicGrounding) ((AtomicProcess) service.getProcess()).getGrounding();			

			URI str = ground.getWSDLParameter(in);
			URI str2 = ground.getWSDLParameter(out);
			
			kb.write(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
