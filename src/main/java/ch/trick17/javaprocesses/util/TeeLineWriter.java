package ch.trick17.javaprocesses.util;

import java.io.IOException;
import java.util.Arrays;

public class TeeLineWriter implements LineWriter {
    
    private final LineWriter[] writers;
    
    public TeeLineWriter(final LineWriter... writers) {
        this.writers = Arrays.copyOf(writers, writers.length);
    }
    
    public void writeLine(final String line) throws IOException {
        for(final LineWriter writer : writers)
            writer.writeLine(line);
    }
}
