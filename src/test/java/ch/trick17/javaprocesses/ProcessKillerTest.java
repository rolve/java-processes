package ch.trick17.javaprocesses;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ProcessKillerTest {
    
    private static final File testFile = new File("target/testfile");
    
    @Test
    public void testKill() throws IOException, InterruptedException {
        testFile.createNewFile();
        assertTrue(testFile.exists());
        
        new JavaProcessBuilder(FileDeleterRunner.class, "don't kill").create()
                .start();
        
        Thread.sleep(5000);
        assertFalse(testFile.exists());
        
        testFile.createNewFile();
        assertTrue(testFile.exists());
        
        new JavaProcessBuilder(FileDeleterRunner.class, "kill").create()
                .start();
        
        Thread.sleep(5000);
        assertTrue(testFile.exists());
        
        testFile.delete();
    }
    
    public static class FileDeleterRunner {
        public static void main(final String[] args) throws IOException {
            final Process process = new JavaProcessBuilder(FileDeleter.class)
                    .create().start();
            
            if(args[0].equals("kill"))
                new ProcessKiller().add(process);
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
