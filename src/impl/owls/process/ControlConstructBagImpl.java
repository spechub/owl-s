// The MIT License
//
// Copyright (c) 2004 Evren Sirin
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

/*
 * Created on Dec 27, 2003
 *
 */
package impl.owls.process;



import impl.owls.generic.list.OWLSObjListImpl;

import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLValue;
import org.mindswap.owl.list.RDFList;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.ControlConstructBag;
import org.mindswap.owls.vocabulary.OWLS;

/**
 * @author Evren Sirin
 *
 */
public class ControlConstructBagImpl extends OWLSObjListImpl implements ControlConstructBag {
    public ControlConstructBagImpl(OWLIndividual ind) {
        super(ind);
        
        setVocabulary(OWLS.CCBag);
    }
        
    public RDFList insert(OWLValue item) {
        ControlConstructBagImpl list = new ControlConstructBagImpl( getOntology().createInstance( vocabulary.List() ) );
        list.setVocabulary(vocabulary);
        list.setFirst( item );
        list.setRest( this );
                
        return list;        
    }
    
    public OWLValue getFirstValue() {
    	OWLIndividual cc = getProperty(vocabulary.first());
    	if (cc != null && !cc.equals(vocabulary.nil()))
    		return (ControlConstruct) cc.castTo(ControlConstruct.class);
    	else
    		return null;
    }
    
    public RDFList getRest() {
        return (ControlConstructBag) getProperty(vocabulary.rest()).castTo(ControlConstructBag.class);
    }
    
	public ControlConstruct constructAt(int index) {
		return (ControlConstruct) get(index);
	}

    public RDFList remove() {
    	ControlConstructBagImpl list = new ControlConstructBagImpl(getOntology().createInstance(vocabulary.List()));
        list.setVocabulary(vocabulary);
        if (size() > 1) {        	
        	list.setFirst(getRest().getFirstValue());        	
        	list.setRest((ControlConstructBag) getRest().getRest());
        } else {
        	return new ControlConstructBagImpl(vocabulary.nil());
        }
                
        return list;
    }
    
    public RDFList remove(OWLValue value) {
    	if ((value == null) || (size() == 0))
    		return this;
    	if (size() == 1)
    		return remove();
    	    	    	
    	RDFList rest = this;
    	int i = 0;
    	while (!rest.isEmpty()) {    		
    		if (rest.getFirstValue().equals(value))     			    				
    			return removeAt(i);
    		i++;
    		rest = rest.getRest();
    	}
    	return this;
    }
    
    public RDFList removeAt(int index) {
        if (index == 0)
            return remove();

        if (index < 0 || isEmpty())
            throw new IndexOutOfBoundsException();
        
        setRest(getRest().removeAt(index - 1));
        return this;
    }
}
