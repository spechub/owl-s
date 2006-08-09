/*
 * Created on Aug 30, 2004
 */
package impl.owls.process;


import impl.owl.CastingList;

import java.net.URI;

import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.process.Binding;
import org.mindswap.owls.process.BindingList;
import org.mindswap.owls.process.Parameter;

/**
 * @author Evren Sirin
 */
public class BindingListImpl extends CastingList implements BindingList {
    public BindingListImpl() {
        super(Binding.class);
    }
    
    public BindingListImpl(OWLIndividualList list) {
        super(list, Binding.class);
    }

    public Binding bindingAt(int index) {
         return (Binding) get(index);
    }

    public Binding getBinding(URI bindingURI) {
        return (Binding) getIndividual(bindingURI);
    }

    public Binding getBindingFor(Parameter param) {
        for(int i = 0; i < size(); i++) {
            Binding binding = bindingAt(i);
            
            if( binding.getParameter().equals( param ) )
                return binding;
        }
        
        return null;
    }
}
