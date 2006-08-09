/*
 * Created on 21.04.2005
 */
package impl.owls.process.execution;

import java.util.Iterator;
import java.util.Vector;

import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.list.RDFList;
import org.mindswap.owls.generic.list.OWLSObjList;
import org.mindswap.owls.process.AnyOrder;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Condition;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.ControlConstructBag;
import org.mindswap.owls.process.ControlConstructList;
import org.mindswap.owls.process.ForEach;
import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.RepeatUntil;
import org.mindswap.owls.process.RepeatWhile;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.ValueOf;
import org.mindswap.owls.process.execution.ThreadedProcessExecutionEngine;
import org.mindswap.owls.process.execution.ThreadedProcessExecutionListener;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.query.ValueMap;
import org.mindswap.swrl.Variable;

/**
 * @author Michael Daenzer, University of Zurich
 */
public class ThreadedProcessExecutionEngineImpl extends ProcessExecutionEngineImpl
		implements Runnable, ThreadedProcessExecutionEngine {	
	
	private static final int DEFAULT_INTERVAL = 5000;
	
	private boolean resultReady = false;
	private boolean interrupted = false;
	private int interval = DEFAULT_INTERVAL;
	
	private Process process = null;
	private ValueMap values = null;
	private ValueMap resultMap = null;
	private String processName;
	
	private Vector execListener = new Vector();
		
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() { 
		executionHasStarted();
	    setResultMap(super.execute(this.process, this.values));
	}
	
	/* (non-Javadoc)
	 * @see org.mindswap.owls.process.execution.ProcessExecutionEngine#execute(org.mindswap.owls.process.Process, org.mindswap.query.ValueMap)
	 */
	public void executeThreaded(Process p, ValueMap values) {
		this.process = p;
		this.processName = p.getName();
		if (this.processName == null)
		    this.processName = p.getLocalName();
		this.values = values;
		execAsynchronous();
	}
	
	// starts a method asynchronously in a separate thread
	public void execAsynchronous() {
		setResultReady(false);
		Thread threadedExec = new Thread(this);
		threadedExec.start();
	}
	
	/**
	 * @see impl.owls.process.execution.ProcessExecutionEngine#createSequence(org.mindswap.owls.process.Sequence)
	 */
	protected void executeSequence(Sequence cc) {
		ControlConstructList ccList = cc.getComponents(); 

		for(int i = 0; i < ccList.size(); i++) {
		    ControlConstruct component = ccList.constructAt(i);

			if (isInterrupted()) 
				processInterruption();
		    
			executeConstruct(component);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see impl.owls.process.execution.ProcessExecutionEngineImpl#executeAnyOrder(org.mindswap.owls.process.AnyOrder)
	 */
	protected void executeAnyOrder(AnyOrder cc) {
		ControlConstructBag ccList = cc.getComponents(); 
		
		// AnyOrder says it doesn't matter in which order subelements
		// are executed so let's try the sequential order		
		// FIXME check preconditions to find a correct ordering
		OWLIndividualList list = ccList.getAll();
		for(int i = 0; i < list.size(); i++) {
		    ControlConstruct component = (ControlConstruct) list.individualAt(i);
		    
			if (isInterrupted()) 
				processInterruption();

			executeConstruct(component);
		}
	}
	
	protected void executeForEach(ForEach cc) {
	    ValueMap parentValues = (ValueMap) performResults.get(OWLS.Process.TheParentPerform);
	    
	    ControlConstruct loopBody = cc.getComponent();
	    Variable loopVar = cc.getLoopVar();
	    ValueOf valueOf = cc.getListValue();
	    
	    Perform otherPerform = valueOf.getPerform();
	    Parameter otherParam = valueOf.getParameter();
	    
	    ValueMap performResult = (ValueMap) performResults.get(otherPerform);	
	    if(performResult == null)
	        throw new ExecutionException( "Perform " + otherPerform + " cannot be found!" );
	    
	    OWLIndividual ind = performResult.getIndividualValue(otherParam);
	    RDFList list = (RDFList) ind.castTo(OWLSObjList.class);
	    
	    for( ; !list.isEmpty(); list = list.getRest() ) {
	        OWLIndividual value = list.getFirst();
	        parentValues.setValue(loopVar, value);
	        
			if (isInterrupted()) 
				processInterruption();
	        
	        executeConstruct(loopBody);
	    }
	}
	
	protected void executeRepeatUntil(RepeatUntil cc) {
	    Condition whileCondition = cc.getCondition();
	    ControlConstruct loopBody = cc.getComponent();
	    
	    do {
			if (isInterrupted()) 
				processInterruption();
			
	        executeConstruct(loopBody);
	    }
	    while( isTrue( whileCondition ) );	
	}
	
	protected void executeRepeatWhile(RepeatWhile cc) {
	    Condition whileCondition = cc.getCondition();
	    ControlConstruct loopBody = cc.getComponent();
	    
	    while( isTrue( whileCondition ) ) {
			if (isInterrupted()) 
				processInterruption();
		
			executeConstruct(loopBody);
	    }
	        
	}
	
	protected ValueMap executeAtomicProcess(AtomicProcess process, ValueMap values) {
		atomicProcessStarted(process);
		ValueMap result = super.executeAtomicProcess(process, values);
		atomicProcessEnded(process);
		return result;
	}
	
    protected ValueMap executePerform(Perform p) {
        ValueMap values = super.executePerform(p);
        
	    Iterator iter = values.getVariables().iterator();
	    while (iter.hasNext()) {
	        Parameter param = (Parameter) iter.next();
	        if (param.isIndividual()) {
	            //OWLIndividual ind = (OWLIndividual) param;
	            //if (ind.isType(NextOnt.nextOutput) || ind.isType(NextOnt.nextInput)) 
	                parameterValueSet(param, values.getStringValue(param));
	        }	        
	    }
	    
	    return values;
    }
	
	public void continueExec() {
		setInterrupted(false);	
		executionContinued();
	}
	
	public void interruptExec() {
		interruptExec(DEFAULT_INTERVAL);
	}
	
    public void interruptExec(int millisToSleep) {
        setInterrupted(true);
        setInterval(millisToSleep);
        executionInterrupted();
    }
	
	protected boolean isInterrupted() {
		return interrupted;
	}
	
	protected void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}
	
	protected void setResultMap(ValueMap results) {
		if (results != null) {
			resultMap = results;
			setResultReady(true);
		}
	}
	
	public ValueMap getResultMap() {
		setResultReady(false);
		return resultMap;
	}
	
	public boolean isResultReady() {
		return resultReady;
	}
	
	public void setResultReady(boolean resultState) {
		resultReady = resultState;
		if (resultState)
			executionHasFinished();
	}
	
	 // waits until the interruption ended
	private void processInterruption() {
		while (isInterrupted()) {
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * invokes executionInterrupted() on all registred listeners
	 */
	protected void executionInterrupted() {
	    Object[] listeners = execListener.toArray();
		for (int i = 0; i < listeners.length; i++) 	 
		    ((ThreadedProcessExecutionListener) listeners[i]).executionInterrupted(process);
	}
	
	/**
	 * invokes executionContinued() on all registred listeners
	 */
	protected void executionContinued() {
		Object[] listeners = execListener.toArray();
		for (int i = 0; i < listeners.length; i++) 						
			((ThreadedProcessExecutionListener) listeners[i]).executionContinued(process);
	}
	
	/**
	 * invokes atomicProcessStarted(AtomicProcess atomicProcess) on all registred listeners
	 * @param atomicProcess the AtomicProcess whichs execution started
	 */
	protected void atomicProcessStarted(AtomicProcess atomicProcess) {
	    Object[] listeners = execListener.toArray();
		for (int i = 0; i < listeners.length; i++)	
		    ((ThreadedProcessExecutionListener) listeners[i]).atomicProcessStarted(atomicProcess);
	}
	
	/**
	 * invokes atomicProcessEnded(AtomicProcess atomicProcess) on all registred listeners
	 * @param atomicProcess the AtomicProcess whichs execution finished
	 */
	protected void atomicProcessEnded(AtomicProcess atomicProcess) {
	    Object[] listeners = execListener.toArray();
		for (int i = 0; i < listeners.length; i++) 		    
		    ((ThreadedProcessExecutionListener) listeners[i]).atomicProcessEnded(atomicProcess);
	}
		
	protected void parameterValueSet(Parameter param, String value) {
	    Object[] listeners = execListener.toArray();
		for (int i = 0; i < listeners.length; i++) 		    
		    ((ThreadedProcessExecutionListener) listeners[i]).parameterValueSet(process, param, value);
	}
	
	protected void executionHasFinished() {
	    Object[] listeners = execListener.toArray();
		for (int i = 0; i < listeners.length; i++) 
		    ((ThreadedProcessExecutionListener) listeners[i]).executionFinished(process);
	}
	
	protected void executionHasStarted() {
	    Object[] listeners = execListener.toArray();
		for (int i = 0; i < listeners.length; i++) 
		    ((ThreadedProcessExecutionListener) listeners[i]).executionStarted(process);
	}
	
    /* (non-Javadoc)
     * @see org.mindswap.owls.process.execution.ThreadedProcessExecutionEngine#addExecutionListener(org.mindswap.owls.process.execution.ThreadedProcessExecutionListener)
     */
    synchronized public void addExecutionListener(ThreadedProcessExecutionListener listener) {
        execListener.add(listener);
    }

    /* (non-Javadoc)
     * @see org.mindswap.owls.process.execution.ThreadedProcessExecutionEngine#removeExecutionListener(org.mindswap.owls.process.execution.ThreadedProcessExecutionListener)
     */
    synchronized public void removeExecutionListener(ThreadedProcessExecutionListener listener) {
        execListener.remove(listener);
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }  

    public String getProcessName() {
        return processName;
    }
    
}
