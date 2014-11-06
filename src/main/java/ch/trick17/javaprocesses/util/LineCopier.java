package ch.trick17.javaprocesses.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.concurrent.Callable;

/**
 * A simple {@link Runnable}/{@link Callable} that copies all text from a
 * {@link BufferedReader} to a {@link LineWriter}, line by line.
 * 
 * @author Michael Faes
 */
public class LineCopier implements Runnable, Callable<Void> {
    
    private final BufferedReader reader;
    private final LineWriter writer;
    
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
    public LineCopier(InputStream in, final LineWriter writer) {
        this(new BufferedReader(new InputStreamReader(in)), writer);
    }
    
    /**
     * Convenience constructor. The given {@link Writer} is wrapped in a
     * {@link LineWriter} adapter.
     * 
     * @param reader
     *            Source
     * @param writer
     *            Destination
     */
    public LineCopier(final BufferedReader reader, Writer writer) {
        this(reader, new DefaultLineWriter(writer));
    }
    
    public LineCopier(final BufferedReader reader, final LineWriter writer) {
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
        while((line = reader.readLine()) != null)
            writer.writeLine(line);
        return null;
    }
    
    private static class DefaultLineWriter implements LineWriter {
        
        static final String lineSeparator = System
                .getProperty("line.separator");
        
        final Writer writer;
        
        DefaultLineWriter(Writer writer) {
            this.writer = writer;
        }
        
        public void writeLine(String line) throws IOException {
            writer.write(line);
            writer.write(lineSeparator);
        }
    }
}
