package ch.trick17.javaprocesses;

import java.util.ArrayList;
import java.util.List;

public class ProcessKiller implements Runnable {
    
    private final List<Process> processes = new ArrayList<Process>();
    
    public List<Process> getProcesses() {
        return processes;
    }
    
    public void run() {
        for(final Process process : processes)
            process.destroy();
    }
}
