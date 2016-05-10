package ch.trick17.javaprocesses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class AutoExitTest {
    
    private static final File testFile = new File("target/testfile");
    
    @Test
    public void testParentTerminates() throws IOException, InterruptedException {
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
            new JavaProcessBuilder(FileDeleter.class, args).start();
            
            /* When this program exits (i.e. now), so should the FileDeleter program (if autoexit is
             * set). */
        }
    }
    
    public static class FileDeleter {
        public static void main(final String[] args)
                throws InterruptedException {
            if(args[0].equals("autoexit"))
                AutoExit.install();
                
            Thread.sleep(2000);
            testFile.delete();
        }
    }
    
    @Test
    public void testChildTerminates() throws IOException, InterruptedException {
        final Process greeter = new JavaProcessBuilder(Greeter.class,
                "no autoexit").start();
        /* Child process should terminate even if the parent is still running */
        
        Thread.sleep(2000);
        assertEquals(0, greeter.exitValue());
    }
    
    public static class Greeter {
        public static void main(final String[] args) {
            AutoExit.install();
            
            System.out.println("Hello World!");
        }
    }
}
