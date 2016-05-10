package ch.trick17.javaprocesses;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a simple way to kill child processes when the JVM shuts down.
 * <p>
 * This class relies on the {@linkplain Runtime#addShutdownHook(Thread) shutdown hooks}, which are
 * not guaranteed to run in all cases. An alternative is {@link AutoExit}, which runs in the child
 * process and kills it when the "connection" to the parent process is lost.
 * 
 * @author Michael Faes
 */
public class AutoProcessKiller extends Thread {
    
    private final List<Process> processes = new ArrayList<Process>();
    
    /**
     * Creates a process killer, which registers itself as a
     * {@linkplain Runtime#addShutdownHook(Thread) shutdown hook}.
     */
    public AutoProcessKiller() {
        Runtime.getRuntime().addShutdownHook(this);
    }
    
    /**
     * Returns the lists of processes that are killed when the JVM shuts down.
     * 
     * @return This process list
     */
    public List<Process> getProcesses() {
        return processes;
    }
    
    /**
     * Convenience method for <code>getProcesses().add(process)</code>.
     * 
     * @param process
     *            The process to be added
     */
    public void add(final Process process) {
        processes.add(process);
    }
    
    /**
     * Kills all processes that are in the {@linkplain #getProcesses() process list}.
     */
    @Override
    public void run() {
        for(final Process process : processes)
            process.destroy();
    }
}
