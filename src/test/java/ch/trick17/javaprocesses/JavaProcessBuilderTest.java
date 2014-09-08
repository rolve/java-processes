package ch.trick17.javaprocesses;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.junit.Test;

import ch.trick17.javaprocesses.util.ByteCopier;
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
        new Thread(new LineCopier(new InputStreamReader(process
                .getInputStream()), output)).start();
        new Thread(new LineCopier(new InputStreamReader(process
                .getErrorStream()), errors)).start();
        
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
    public void testKillOnShutdown() throws IOException, InterruptedException {
        testFile.createNewFile();
        assertTrue(testFile.exists());
        
        Process process = new JavaProcessBuilder(FileDeleterRunner.class,
                "don't kill").redirectErrorStream(true).start();
        new Thread(new ByteCopier(process.getInputStream(), System.out))
                .start();
        
        Thread.sleep(5000);
        assertFalse(testFile.exists());
        
        testFile.createNewFile();
        assertTrue(testFile.exists());
        
        process = new JavaProcessBuilder(FileDeleterRunner.class, "kill")
                .redirectErrorStream(true).start();
        new Thread(new ByteCopier(process.getInputStream(), System.out))
                .start();
        
        Thread.sleep(5000);
        assertTrue(testFile.exists());
        
        testFile.delete();
    }
    
    public static class FileDeleterRunner {
        public static void main(final String[] args) throws IOException {
            final JavaProcessBuilder builder = new JavaProcessBuilder(
                    FileDeleter.class).redirectErrorStream(true);
            if(args[0].equals("kill"))
                builder.killOnShutdown(true);
            
            builder.start();
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
