package ch.trick17.javaprocesses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
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
        final Process process = new JavaProcessBuilder(Greeter.class, "Michael")
                .start();
        
        final StringWriter output = new StringWriter();
        final StringWriter errors = new StringWriter();
        new Thread(new LineCopier(new BufferedReader(new InputStreamReader(
                process.getInputStream())), output)).start();
        new Thread(new LineCopier(new BufferedReader(new InputStreamReader(
                process.getErrorStream())), errors)).start();
        
        process.waitFor();
        assertEquals("", errors.toString());
        assertEquals("Hello Michael!" + lineSeparator, output.toString());
    }
    
    public static class Greeter {
        public static void main(final String[] args) {
            System.out.println("Hello " + args[0] + "!");
        }
    }
    
    private static final File testFile = new File("target/testfile");
    
    @Test
    public void testAutoExit() throws IOException, InterruptedException {
        testFile.createNewFile();
        assertTrue(testFile.exists());
        
        new JavaProcessBuilder(FileDeleterRunner.class, "no autoexit").start();
        
        Thread.sleep(5000);
        assertFalse(testFile.exists());
        
        testFile.createNewFile();
        assertTrue(testFile.exists());
        
        new JavaProcessBuilder(FileDeleterRunner.class, "autoexit").start();
        
        Thread.sleep(5000);
        assertTrue(testFile.exists());
        
        testFile.delete();
    }
    
    public static class FileDeleterRunner {
        public static void main(final String[] args) throws IOException {
            if(args[0].equals("autoexit"))
                new JavaProcessBuilder(FileDeleter.class).autoExit(true)
                        .start();
            else
                new JavaProcessBuilder(FileDeleter.class).start();
            
            /* When this program exits (i.e. now), so should the AutoExitProgram */
        }
    }
    
    public static class FileDeleter {
        public static void main(final String[] args)
                throws InterruptedException {
            Thread.sleep(2000);
            testFile.delete();
        }
    }
}
