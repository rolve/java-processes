package ch.trick17.javaprocesses.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

/**
 * A simple {@link Runnable}/{@link Callable} that forwards all text from a
 * {@link BufferedReader} to a {@link Logger}. This is done line by line.
 * 
 * @author Michael Faes
 */
public class LineLogger implements Runnable, Callable<Void> {
    
    private final BufferedReader reader;
    private final Logger logger;
    
    /**
     * Convenience constructor. The given {@link InputStream} is wrapped in an
     * {@link InputStreamReader} (with the default charset) and in a
     * {@link BufferedReader}.
     * 
     * @param in
     *            Source
     * @param logger
     *            Destination
     */
    public LineLogger(InputStream in, final Logger logger) {
        this(new InputStreamReader(in), logger);
    }
    
    /**
     * Convenience constructor. The given {@link Reader} is wrapped in a
     * {@link BufferedReader}.
     * 
     * @param reader
     *            Source
     * @param logger
     *            Destination
     */
    public LineLogger(final Reader reader, final Logger logger) {
        this(new BufferedReader(reader), logger);
    }
    
    public LineLogger(final BufferedReader reader, final Logger logger) {
        this.reader = reader;
        this.logger = logger;
    }
    
    /**
     * Does the forwarding.
     * 
     * @throws RuntimeException
     *             If an {@link IOException} occurs
     */
    public void run() {
        try {
            call();
        } catch(final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Does the forwarding.
     */
    public Void call() throws IOException {
        String line;
        while((line = reader.readLine()) != null)
            logger.info(line);
        return null;
    }
}
