package ch.trick17.javaprocesses.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

/**
 * A simple {@link Runnable}/{@link Callable} that copies all data from an
 * {@link InputStream} to an {@link OutputStream}. This is done byte by byte, so
 * it is a good idea to use buffered streams.
 * 
 * @author Michael Faes
 */
public class ByteCopier implements Runnable, Callable<Void> {
    
    private final InputStream in;
    private final OutputStream out;
    
    public ByteCopier(final InputStream in, final OutputStream out) {
        this.in = in;
        this.out = out;
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
        int data;
        while((data = in.read()) != -1)
            out.write(data);
        return null;
    }
}
