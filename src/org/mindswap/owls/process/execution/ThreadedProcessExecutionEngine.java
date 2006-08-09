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
 * Created on Apr 25, 2005
 */
package org.mindswap.owls.process.execution;

import org.mindswap.owls.process.Process;
import org.mindswap.query.ValueMap;

/**
 * <p>This class subclasses the default <code>ProcessExecutionEngine</code> and adds support 
 * for threaded Execution of a process or perform.</p> 
 * 
 * <p>Logically interruptable (means nested) ControlConstruct's check before the Execution
 * of every SubControlConstruct if the Execution was interrupted (with a call of interrupt()) 
 * by another thread and wait until the interruption ends (with a call of unfreeze()). </p> 
 * 
 * <p>Because of the asynchronous execution the results cannot be returned directly to the 
 * caller. Results can be fetched in two different ways. 
 * <ul>
 * 	<li>A ThreadedProcessExecutionListener is used and the resultReady() method is 
 * 		overwritten to get the result with a call of getResultMap()</li>
 *	<li>A custom wait loop is implemented, which polls on isResultReady() and fetchs 
 *		the result when the answer is positive.</li>
 *</ul></p> 
 * 
 * @author Michael Daenzer, University of Zurich
 * @see org.mindswap.owls.process.execution.ProcessExecutionEngine  
 * @see org.mindswap.owls.process.execution.ThreadedProcessExecutionListener
 */

public interface ThreadedProcessExecutionEngine extends ProcessExecutionEngine {
	
    /**
     * Executes the given process in a seperate thread.
     * 
     * @param process the process to execute
     * @param values a <code>ValueMap</code> containing the values for all inputs and locals.
     */
    public void executeThreaded(Process process, ValueMap values);
    
	/**
	 * Finishes the interruption of the execution. 
	 * The Execution is not continued immediately!
	 */
	public void continueExec();

	/**
	 * Interrupts the Execution after the execution of the 
	 * current atomic entity has finished and until unfreeze() is invoked.
	 */
	public void interruptExec();
	
	/**
	 * Interrupts the Execution after the execution of the 
	 * current atomic entity has finished and until unfreeze() is invoked.
	 * 
	 * @param millisToSleep Interval in millisecond for which the ExecutionEngine sleeps before checking the interruption state
	 */
	public void interruptExec(int millisToSleep);

	/**
	 * Returns the result of an execution (or null if there is no result). Due to the asynchronous
	 * execution the result cannot be passed back to the caller, but must be fetched with a call of this method
	 *  
	 * @return A <code>ValueMap</code> containing the results of the execution
	 */
	public ValueMap getResultMap();

	/**
	 * Indicates, if the execution has finished and the result is available
	 * 
	 * @return true, if execution finished and a result is avalaible. false, otherwise
	 */
	public boolean isResultReady();

    /**
     * Adds the listener to the listener list
     * @param listener the listener to add to the registred listeners
     */
    public void addExecutionListener(ThreadedProcessExecutionListener listener);
    
    /**
     * Removes the listener from the listener list
     * @param listener the listener to remove to the registred listeners
     */
    public void removeExecutionListener(ThreadedProcessExecutionListener listener);
}