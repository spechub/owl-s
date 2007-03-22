package impl.owls.grounding;

import org.mindswap.owl.OWLClass;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLTransformator;

/**
 * 
 * @author Michael Dänzer, University of Zurich
 * @date 20.03.2007
 */
public abstract class JavaClassTransformator implements OWLTransformator {
	protected OWLClass owlClass;
	protected Class javaClass;
	protected OWLKnowledgeBase kb;	

	public JavaClassTransformator() {
		super();
		this.kb = OWLFactory.createKB();
	}
	
	public OWLClass getOWLClass() { 
		return owlClass;
	}
	
	public Class getJavaClass() {
		return javaClass;
	}

	private Class classFromString(String className) {
		Class claz;
		try {
			claz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			claz = null;
		}
		return claz;
	}
	
	public abstract Object transformFromOWL(OWLIndividual ind);

	public abstract OWLIndividual transformToOWL(Object object);
}
