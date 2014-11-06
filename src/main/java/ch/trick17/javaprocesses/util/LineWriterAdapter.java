package ch.trick17.javaprocesses.util;

import java.io.IOException;
import java.io.Writer;

public class LineWriterAdapter implements LineWriter {
    
    private static final String lineSeparator = System
            .getProperty("line.separator");
    
    private final Writer writer;
    
    public LineWriterAdapter(Writer writer) {
        this.writer = writer;
    }
    
    public void writeLine(String line) throws IOException {
        writer.write(line);
        writer.write(lineSeparator);
    }
}
