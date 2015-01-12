package ch.trick17.javaprocesses.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class LineWriterAdapter implements LineWriter {
    
    private static final String lineSeparator = System
            .getProperty("line.separator");
    
    private final Writer writer;
    
    /**
     * Convenience constructor that creates a (buffered)
     * {@link OutputStreamWriter} from the given stream with the default
     * charset.
     * 
     * @param out
     *            The target stream
     */
    public LineWriterAdapter(OutputStream out) {
        this(new BufferedWriter(new OutputStreamWriter(out)));
    }
    
    public LineWriterAdapter(Writer writer) {
        this.writer = writer;
    }
    
    public void writeLine(String line) throws IOException {
        writer.write(line);
        writer.write(lineSeparator);
        writer.flush();
    }
}
