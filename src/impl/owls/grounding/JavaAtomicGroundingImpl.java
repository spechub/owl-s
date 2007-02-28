/*
 * Created on 13.04.2005
 */
package impl.owls.grounding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.EntityFactory;
import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLObject;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.grounding.AtomicGrounding;
import org.mindswap.owls.grounding.JavaAtomicGrounding;
import org.mindswap.owls.grounding.JavaParameter;
import org.mindswap.owls.grounding.JavaVariable;
import org.mindswap.owls.grounding.MessageMapList;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.vocabulary.MoreGroundings;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.query.ValueMap;
import org.mindswap.utils.ReflectionHelpers;
import org.mindswap.utils.XSLTEngine;

/**
 * A JavaAtomicGrounding grounds an OWL-S Service to a Java method invocation. The method call
 * is specified by its method signature in an OWL-S Ontology. The driving parts are:
 * <ul>
 * 	<li>fully qualified class name</li>
 * 	<li>method name</li>
 * 	<li>a map of all input parameters (at the time only primitive datatypes and their adapter classes)</li>
 * 	<li>an output type (at the time only primitive datatypes and their adapter classes)</li>
 * </ul>
 * 
 * @author Michael Daenzer, University of Zürich
 * 
 * @see <a href="http://www.ifi.unizh.ch/ddis/ont/owl_s/MoreGroundings.owl">Grounding Ontology</a>
 * @see org.mindswap.owls.grounding.AtomicGrounding
 * @see org.mindswap.owls.vocabulary.NextOnt
 * @see impl.owls.grounding.AtomicGroundingImpl
 */
public class JavaAtomicGroundingImpl extends AtomicGroundingImpl implements JavaAtomicGrounding {
    OWLIndividualList inParamList = null;
    
    /**
     * @param ind
     */
    public JavaAtomicGroundingImpl(OWLIndividual ind) {
        super(ind);
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.grounding.JavaAtomicGrounding#setClaz(java.lang.String)
     */
    public void setClaz(String claz) {
        setProperty(MoreGroundings.javaClass, claz);
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.grounding.JavaAtomicGrounding#getClaz()
     */
    public String getClaz() {
        return getPropertyAsString(MoreGroundings.javaClass);
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.grounding.JavaAtomicGrounding#setMethod(java.lang.String)
     */
    public void setMethod(String method) { 
        setProperty(MoreGroundings.javaMethod, method);
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.grounding.JavaAtomicGrounding#getMethod()
     */
    public String getMethod() {
        return getPropertyAsString(MoreGroundings.javaMethod);
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.grounding.AtomicGrounding#getDescriptionURL()
     */
    public URL getDescriptionURL() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.grounding.AtomicGrounding#addMessageMap(org.mindswap.owls.process.Parameter, java.lang.String, java.lang.String)
     */
    public void addMessageMap(Parameter owlsParameter,
            String groundingParameter, String xsltTransformation) {
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.grounding.AtomicGrounding#getInputMap()
     */
    public MessageMapList getInputMap() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.grounding.AtomicGrounding#getOutputMap()
     */
    public MessageMapList getOutputMap() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.grounding.AtomicGrounding#invoke(org.mindswap.query.ValueMap)
     */
    public ValueMap invoke(ValueMap values) throws ExecutionException {
        return invoke(values, OWLFactory.createKB());
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.grounding.AtomicGrounding#invoke(org.mindswap.query.ValueMap, org.mindswap.owl.OWLKnowledgeBase)
     */
    public ValueMap invoke(ValueMap values, OWLKnowledgeBase kb) throws ExecutionException {       
        Class claz = null;
        Method method = null;
        Class params[] = null;
        Object paramValues[] = null;
        ValueMap results = new ValueMap();
        Object result = null;
        
        // get and check class
        String strClaz = getClaz();
        if ((strClaz == null) || (strClaz.equals("")))
            throw new ExecutionException("JavaAtomicGrounding: No Java Class defined in Grounding!");
        // get and check method
        String strMethod = getMethod();
        if ((strMethod == null) || (strMethod.equals("")))
            throw new ExecutionException("JavaAtomicGrounding: No Java Method defined in Grounding!");        

        // TODO dmi get types from owl paramtype
        // prepare parameters
        params = new Class[getInputOWLSParamsSize()];
        for (int i = 0; i < getInputOWLSParamsSize(); i++) {
        	params[i] = getInputJavaParamAt(i);
        }
        
        // get class and method reference
        try {
            claz = Class.forName(strClaz);
            method = claz.getDeclaredMethod(strMethod, params);
        } catch (ClassNotFoundException e){
            throw new ExecutionException("JavaAtomicGrounding: Class " + strClaz + " defined in Grounding not found." );
        } catch (NoSuchMethodException e) {
        	throw new ExecutionException("JavaAtomicGrounding: Method " + strMethod + " defined in Grounding not found." );
        } catch (Exception e) {
        	throw new ExecutionException("JavaAtomicGrounding: " + e.getClass().toString() + " ocurred: " + e.getMessage());
        }
        
        // prepare inputs
        paramValues = new Object[getInputOWLSParamsSize()];
        for (int i = 0; i < params.length; i++) {
        	OWLValue owlValue = values.getValue(getInputOWLSParamAt(i));
        	if (owlValue.isDataValue())        		
        		paramValues[i] = ReflectionHelpers.getCastedObjectFromStringValue(((OWLDataValue) owlValue).getLexicalValue(), params[i]);
            else {
                String xslt = getTransformationAt(i);
                if(xslt != null) {
                    String rdf = ((OWLIndividual) owlValue).toRDF();
                    String xsltResult = XSLTEngine.transform(rdf, xslt, values);
                    paramValues[i] = xsltResult.trim();
                }               
                else
                    paramValues[i] = ((OWLIndividual) owlValue).toString();
            }
            //System.out.println( "Parameter " + i + " " + paramValues[i] );
        }
        
        // invoke java method
        try {
            Object obj = claz.newInstance();
            if (method.getReturnType().toString().equalsIgnoreCase("void"))
            	method.invoke(obj, paramValues);
            else
            	result = method.invoke(obj, paramValues);
        } catch (InvocationTargetException e) {
        	throw new ExecutionException("JavaAtomicGrounding: Error in executed method\n" + e.getTargetException().toString() + " ocurred: ");
        } catch (Exception e) {
        	throw new ExecutionException("JavaAtomicGrounding: Error while executing method\n" + e.getClass().toString() + " ocurred: ");
        }
        
        // set output
        if (result != null) {
            Parameter param = getOutputOWLSParam();
            if (param == null) 
            	throw new ExecutionException("JavaAtomicGrounding: Output in Grounding not specified although method provides a return value.");            
            if(param.getParamType().isDataType())
		    	results.setValue(param, EntityFactory.createDataValue(result));
			else 
				results.setValue(param, kb.getBaseOntology().parseLiteral(result.toString()));
        }
        
        return results;
    }

    // returns the OWL-S Output Parameter
	private Parameter getOutputOWLSParam() {	
		OWLObject owlsParam = getProperty(MoreGroundings.javaOutput).getProperty(MoreGroundings.owlsParameter);
		return (owlsParam == null) ? null : (Parameter) owlsParam.castTo(Parameter.class);
    }
	
	// Implements the Singleton Pattern for the private field inParamList 
    private void createInputOWLSParamList() {
    	if (inParamList == null)
            inParamList = getProperties(MoreGroundings.hasJavaParameter);
    }

    // returns the n-th OWL-S Input Parameter
    private Parameter getInputOWLSParamAt(int index) {
        createInputOWLSParamList();

        // TODO dmi throw exception
        if (index >= getInputOWLSParamsSize())
            return null;
        
        for (int i = 0; i < getInputOWLSParamsSize(); i++) {
            OWLDataValue paramIndex = inParamList.individualAt(i).getProperty(MoreGroundings.paramIndex);
            if (paramIndex.getLexicalValue().equals(Integer.toString(index + 1))) {
                OWLObject owlsParam = inParamList.individualAt(i).getProperty(MoreGroundings.owlsParameter);
                return (owlsParam == null) ? null : (Parameter) owlsParam.castTo(Parameter.class); 
            }
        }
        return null;
    }

    // returns the n-th XSLT transformtion
    private String getTransformationAt(int index) {
        createInputOWLSParamList();

        // TODO dmi throw exception
        if (index >= getInputOWLSParamsSize())
        	return null;
        
        for (int i = 0; i < getInputOWLSParamsSize(); i++) {
        	OWLDataValue paramIndex = inParamList.individualAt(i).getProperty(MoreGroundings.paramIndex);
        	if (paramIndex.getLexicalValue().equals(Integer.toString(index + 1))) {
                OWLDataValue xslt = inParamList.individualAt(i).getProperty(OWLS.Grounding.xsltTransformationString);
                return (xslt == null) ? null : xslt.toString();
        	}
        }
        return null;
    }
    
    // returns the n-th Parameter of the Java Method specified in the OWL-S JavaAtomicProcessGrounding
    private Class getInputJavaParamAt(int index) {        
    	createInputOWLSParamList();
    	
        // TODO dmi throw exception
        if (index >= getInputOWLSParamsSize())
        	return null;
        
        for (int i = 0; i < getInputOWLSParamsSize(); i++) {
        	OWLDataValue paramIndex = inParamList.individualAt(i).getProperty(MoreGroundings.paramIndex);
        	if (paramIndex.getLexicalValue().equals(Integer.toString(index + 1))) {
        		 String javaType = inParamList.individualAt(i).getProperty(MoreGroundings.javaType).getValue().toString();
        	     return ReflectionHelpers.getClassFromString(javaType);
        	}
        }
        return null;
        
       
    }
    
    // returns the number of OWL-S Input Parameters   
    private int getInputOWLSParamsSize() {
    	createInputOWLSParamList();
    	return inParamList.size();
    }

	/* (non-Javadoc)
	 * @see org.mindswap.owls.grounding.JavaAtomicGrounding#setOutputVar(java.lang.String, java.lang.String, org.mindswap.owls.process.Output)
	 */
	public void setOutputVar(String name, String type, Output owlsParameter) {
		OWLIndividual ind = getOntology().createInstance(MoreGroundings.JavaVariable, URI.create(name));
		ind.setProperty(MoreGroundings.javaType, type);
		ind.setProperty(MoreGroundings.owlsParameter, owlsParameter);
		setProperty(MoreGroundings.javaOutput, ind);
	}

	/* (non-Javadoc)
	 * @see org.mindswap.owls.grounding.JavaAtomicGrounding#setInputVar(java.lang.String, java.lang.String, int, org.mindswap.owls.process.Input)
	 */
	public void setInputParameter(String name, String type, int index, Input owlsParameter) {
		OWLIndividual ind = getOntology().createInstance(MoreGroundings.JavaParameter, URI.create(name));
		ind.setProperty(MoreGroundings.javaType, type);
		ind.setProperty(MoreGroundings.owlsParameter, owlsParameter);
		ind.setProperty(MoreGroundings.paramIndex, Integer.toString(index));
		addProperty(MoreGroundings.hasJavaParameter, ind);
	}

	@Override
	public String getGroundingType() {
		return AtomicGrounding.GROUNDING_JAVA;
	}
	
	@Override
	public String toString() {
		return getClaz() + "." + getMethod();
	}

	private void removeAll() {
		if (hasProperty(MoreGroundings.javaClass))
			removeProperties(MoreGroundings.javaClass);
		if (hasProperty(MoreGroundings.javaMethod))
			removeProperties(MoreGroundings.javaMethod);
		
		// TODO an rdf:type property of the related input stays persistent. why????
		if (hasProperty(MoreGroundings.hasJavaParameter)) {
			OWLIndividualList indList = getProperties(MoreGroundings.hasJavaParameter);
			for (int i = 0; i < indList.size(); i++) {
				OWLIndividual ind = indList.individualAt(i);
				if (ind.hasProperty(MoreGroundings.javaType))
					ind.removeProperties(MoreGroundings.javaType);
				if (ind.hasProperty(MoreGroundings.owlsParameter))
					ind.removeProperties(MoreGroundings.owlsParameter);
				if (ind.hasProperty(MoreGroundings.paramIndex))
					ind.removeProperties(MoreGroundings.paramIndex);
				removeProperty(MoreGroundings.hasJavaParameter, ind);
				ind.delete();				
			}
		}
		
		if (hasProperty(MoreGroundings.javaOutput)) {
			OWLIndividual ind = getProperty(MoreGroundings.javaOutput);
			if (ind.hasProperty(MoreGroundings.javaType))
				ind.removeProperties(MoreGroundings.javaType);
			if (ind.hasProperty(MoreGroundings.owlsParameter))
				ind.removeProperties(MoreGroundings.owlsParameter);
			removeProperties(MoreGroundings.javaOutput);
			ind.delete();
		}
		
		if (hasProperty(MoreGroundings.owlsProcess))
			removeProperties(MoreGroundings.owlsProcess);
		if (hasProperty(OWLS.Grounding.owlsProcess))
			removeProperties(OWLS.Grounding.owlsProcess);	
	}
	
	@Override
	public void delete() {		
		removeAll();			
		
		super.delete();
	}

	public JavaParameter getInputParamter(Input input) {
		OWLIndividualList list = getPropertiesAs(MoreGroundings.hasJavaParameter, JavaParameter.class);
		for (int i = 0; i < list.size(); i++) {
			if (list.individualAt(i).getURI().equals(input.getURI()))
				return (JavaParameter) list.individualAt(i);
		}
		return null;
	}

	public JavaVariable getOutputVariable() {
		return (JavaVariable) getPropertyAs(MoreGroundings.javaOutput, JavaVariable.class);
	}		
}


