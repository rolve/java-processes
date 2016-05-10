package ch.trick17.javaprocesses;

import static ch.trick17.javaprocesses.JavaProcessBuilder.mainMethodOf;
import static java.util.Arrays.copyOfRange;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A class that can be used to start a program with {@link AutoExit} enabled. The name of the main
 * class to start is expected to be provided as the first command line argument.
 * 
 * @see #main(String[])
 * @author Michael Faes
 */
public class AutoExitProgram {
    
    /**
     * This main method {@linkplain AutoExit#install() installs} an {@link AutoExit} instance and
     * then calls the main method of the class given by the first command line argument. The rest of
     * the arguments are passed to that main method.
     * 
     * @param args
     *            Command line arguments. The first argument must be the name of the main class to
     *            start.
     * @throws Throwable
     *             If anything goes wrong when trying to invoke the main method
     */
    public static void main(final String[] args) throws Throwable {
        AutoExit.install();
        
        if(args.length < 1)
            throw new IllegalArgumentException("No main class provided.");
            
        final Method main = mainMethodOf(Class.forName(args[0]));
        final String[] newArgs = copyOfRange(args, 1, args.length);
        
        try {
            main.invoke(null, new Object[]{newArgs});
        } catch(final InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
