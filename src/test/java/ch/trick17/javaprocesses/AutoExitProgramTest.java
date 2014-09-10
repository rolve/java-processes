package ch.trick17.javaprocesses;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class AutoExitProgramTest {
    
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
            if(args[0].equals("autoexit"))
                new JavaProcessBuilder(AutoExitProgram.class, FileDeleter.class
                        .getName()).start();
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
