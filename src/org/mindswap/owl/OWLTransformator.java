package org.mindswap.owl;

/**
 * 
 * @author Michael Dänzer, University of Zurich
 * @date 20.03.2007
 */
public interface OWLTransformator {
	public OWLIndividual transformToOWL(Object object);
	
	public Object transformFromOWL(OWLIndividual ind);
	
	public OWLClass getOWLClass();
}
