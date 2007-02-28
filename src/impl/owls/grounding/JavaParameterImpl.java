package impl.owls.grounding;

import org.mindswap.owl.OWLIndividual;
import org.mindswap.owls.grounding.JavaParameter;
import org.mindswap.owls.vocabulary.MoreGroundings;

public class JavaParameterImpl extends JavaVariableImpl implements
		JavaParameter {

	public JavaParameterImpl(OWLIndividual ind) {
		super(ind);
	}

	public String getParameterIndex() {
		return null;
	}

	public void removeParameterIndex() {
		if (hasProperty(MoreGroundings.paramIndex))
			removeProperties(MoreGroundings.paramIndex);
	}

	public void setParameterIndex(int index) {
		setProperty(MoreGroundings.paramIndex, index);
	}

}
