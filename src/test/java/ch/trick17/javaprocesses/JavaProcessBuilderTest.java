package ch.trick17.javaprocesses;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.junit.Test;

import ch.trick17.javaprocesses.util.LineCopier;

public class JavaProcessBuilderTest {
    
    private static final String lineSeparator = System
            .getProperty("line.separator");
    
    @Test
    public void testStart() throws IOException, InterruptedException {
        final Process process = new JavaProcessBuilder(TestClass.class).start();
        
        final StringWriter output = new StringWriter();
        final StringWriter errors = new StringWriter();
        new Thread(new LineCopier(new InputStreamReader(process
                .getInputStream()), output)).start();
        new Thread(new LineCopier(new InputStreamReader(process
                .getErrorStream()), errors)).start();
        
        process.waitFor();
        assertEquals("Hello World!" + lineSeparator, output.toString());
        assertEquals("", errors.toString());
    }
    
    public static class TestClass {
        
        public static void main(final String[] args) {
            System.out.println("Hello World!");
        }
    }
}
