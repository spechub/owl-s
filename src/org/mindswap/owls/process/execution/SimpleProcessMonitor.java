/*
 * Created on Jun 27, 2005
 */
package org.mindswap.owls.process.execution;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.Process;
import org.mindswap.query.ValueMap;

/**
 * A simple process monitor implementation that prints the progress to console
 * 
 * @author Evren Sirin
 *
 */
public class SimpleProcessMonitor implements ProcessMonitor {
    private PrintWriter out;
    private int monitorFilter;
    
    public SimpleProcessMonitor() {
        this( new PrintWriter( System.out ) );
    }

    public SimpleProcessMonitor( Writer writer ) {
        monitorFilter = Process.ANY;
        
        setWriter( writer );
    }

    public void setWriter( Writer writer ) {
        this.out = (writer instanceof PrintWriter) 
        	? (PrintWriter) writer 
        	: new PrintWriter( writer );
    }
    
    public void executionStarted() {        
        out.println();
        out.flush();
    }
    
    public void executionFinished() {
        out.println();
        out.flush();
    }
    
    public void executionStarted(Process process, ValueMap inputs) {
        out.println( "Start executing process " + process );
        out.flush();
    }

    public void executionFinished(Process process, ValueMap inputs, ValueMap outputs) {
        out.println( "Execution finished for " + process );
        out.flush();
    }

    public void executionFailed(ExecutionException e) {
        out.println( "Execution failed: ");
        out.println( e );
        out.flush();
    }

    public void setMonitorFilter(int processType) {
        monitorFilter = processType;
    }

    public int getMonitorFilter() {
        return monitorFilter;
    }
}
