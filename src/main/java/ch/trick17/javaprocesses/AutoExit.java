package ch.trick17.javaprocesses;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread that can be used to automatically {@linkplain System#exit(int) shut
 * the JVM down} when its parent process is terminated.
 * <p>
 * This is implemented by blocking the thread using a call to
 * <code>System.in.read()</code>. When the parent process terminates, the
 * {@link InputStream#read() read()} method returns and the thread goes on to
 * shut down.
 * <p>
 * Obviously, this class should not be used if the standard input stream is used
 * for something else. If this is the case, an error message is
 * {@linkplain Logger#error(String) logged}.
 * 
 * @author Michael Faes
 */
public class AutoExit extends Thread {
    
    private static final Logger logger = LoggerFactory
            .getLogger(AutoExit.class);
    
    /**
     * Starts an {@link AutoExit} instance. This can also be done manually:
     * 
     * <pre>
     * new AutoExit().start();
     */
    public static void install() {
        new AutoExit().start();
    }
    
    public AutoExit() {
        setDaemon(true);
    }
    
    @Override
    public void run() {
        try {
            final int read = System.in.read();
            if(read != -1)
                logger.error("The standard input stream returned some data. This "
                        + "class should not be used if the standard input stream "
                        + "is used for anything other than automatic shutdown.");
            
        } catch(final IOException e) {}
        System.exit(0);
    }
}
