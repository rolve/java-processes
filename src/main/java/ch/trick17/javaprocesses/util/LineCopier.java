package ch.trick17.javaprocesses.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.Callable;

/**
 * A simple {@link Runnable}/{@link Callable} that copies all text from a
 * {@link BufferedReader} to a {@link Writer}. This is done line by line.
 * 
 * @author Michael Faes
 */
public class LineCopier implements Runnable, Callable<Void> {
    
    private static final String lineSeparator = System
            .getProperty("line.separator");
    
    private final BufferedReader reader;
    private final Writer writer;
    
    /**
     * Convenience constructor. The given {@link InputStream} is wrapped in an
     * {@link InputStreamReader} (with the default charset) and in a
     * {@link BufferedReader}.
     * 
     * @param in
     *            Source
     * @param writer
     *            Destination
     */
    public LineCopier(InputStream in, final Writer writer) {
        this(new InputStreamReader(in), writer);
    }
    
    /**
     * Convenience constructor. The given {@link Reader} is wrapped in a
     * {@link BufferedReader}.
     * 
     * @param reader
     *            Source
     * @param writer
     *            Destination
     */
    public LineCopier(final Reader reader, final Writer writer) {
        this(new BufferedReader(reader), writer);
    }
    
    public LineCopier(final BufferedReader reader, final Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }
    
    /**
     * Does the copying.
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
     * Does the copying.
     */
    public Void call() throws IOException {
        String line;
        while((line = reader.readLine()) != null) {
            writer.write(line);
            writer.write(lineSeparator);
        }
        return null;
    }
}
