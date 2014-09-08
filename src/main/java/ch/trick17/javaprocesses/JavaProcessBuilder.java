package ch.trick17.javaprocesses;

import static java.util.Arrays.asList;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class to build {@link ProcessBuilder}s for Java processes. So, yes,
 * instances of this class are builder builders.
 * <p>
 * This class provides a simple way to start other Java programs. By default,
 * the Java executable, the classpath, the working directory and the environment
 * are the same as for the current Java process, but can be changed if desired.
 * <p>
 * Note that this class assumes that the Java executable is located at
 * <code><em>[java_home]</em>/bin/java</code>.
 * <p>
 * The design of this class' interface mostly follows the {@link ProcessBuilder}
 * class.
 * 
 * @author Michael Faes
 */
public class JavaProcessBuilder {
    
    private static final String javaExe = File.separator + "bin"
            + File.separator + "java";
    
    private final String mainClass;
    private final List<String> args;
    
    private String javaHome = System.getProperty("java.home");
    private String classpath = System.getProperty("java.class.path");
    private List<String> vmArgs = ManagementFactory.getRuntimeMXBean()
            .getInputArguments();
    
    /**
     * Creates a builder for Java process builders with the given main class and
     * an empty argument list.
     * 
     * @param mainClass
     *            The main class
     */
    public JavaProcessBuilder(final Class<?> mainClass) {
        this(mainClass, Collections.<String> emptyList());
    }
    
    /**
     * Creates a builder for Java process builders with the given main class and
     * arguments. This is a convenience constructor that sets the arguments to a
     * string list containing the same strings as the given array, in the same
     * order.
     * 
     * @param mainClass
     *            The main class
     * @param args
     *            The arguments for the main class
     */
    public JavaProcessBuilder(final Class<?> mainClass, final String... args) {
        this(mainClass, new ArrayList<String>(asList(args)));
    }
    
    /**
     * Creates a builder for Java process builders with the given main class and
     * arguments. This constructor does <i>not</i> make a copy of the
     * <code>args</code> list. Subsequent updates to the list will be reflected
     * in the state of this builder (but not in the state of actual process
     * builders).
     * 
     * @param mainClass
     *            The main class
     * @param args
     *            The arguments for the main class
     */
    public JavaProcessBuilder(final Class<?> mainClass, final List<String> args) {
        this(checkMainClass(mainClass), args);
    }
    
    private static String checkMainClass(final Class<?> mainClass) {
        final String name = mainClass.getName();
        try {
            final Method main = mainClass.getMethod("main", String[].class);
            final int modifiers = main.getModifiers();
            if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)
                    || main.getReturnType() != Void.TYPE)
                throw new NoSuchMethodException();
        } catch(final NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + name
                    + " does not have a \"public static void main(String[])\""
                    + " method and therefore cannot be started in a process.");
        }
        return name;
    }
    
    /**
     * Creates a builder for Java process builders with the given main class and
     * arguments. If the class is in the current classpath, the
     * {@link #JavaProcessBuilder(Class)} constructor should be used instead, as
     * it checks if the class is a valid main class.
     * 
     * @param mainClass
     *            The {@linkplain Class#getCanonicalName() canonical name} of
     *            the main class.
     * @param args
     *            The arguments for the main class
     */
    public JavaProcessBuilder(final String mainClass, final List<String> args) {
        this.mainClass = mainClass;
        this.args = args;
    }
    
    /**
     * Returns this builder's Java home directory. Java subprocesses will use
     * the Java executable in this directory.
     *
     * @return This builder's Java home directory
     */
    public String javaHome() {
        return javaHome;
    }
    
    /**
     * Sets this builder's Java home directory. Java subprocesses will use the
     * Java executable in this directory. The argument may be <code>null</code>
     * &ndash; this means to use the Java home directory of the current Java
     * process, defined by the system property <code>java.home</code>, as the
     * Java home directory of the child process.</p>
     *
     * @param home
     *            The new Java home directory
     * @return This builder
     */
    public JavaProcessBuilder javaHome(final String home) {
        if(home == null)
            javaHome = System.getProperty("java.home");
        else
            javaHome = home;
        return this;
    }
    
    /**
     * Returns this builder's classpath. Java subprocesses will use this as
     * their classpath.
     *
     * @return This builder's classpath
     */
    public String classpath() {
        return classpath;
    }
    
    /**
     * Sets this builder's classpath. Java subprocesses will use this as their
     * classpath. The argument may be <code>null</code> &ndash; this means to
     * use the classpath of the current Java process, defined by the system
     * property <code>java.class.path</code>, as the classpath of the child
     * process.</p>
     *
     * @param cp
     *            The new classpath
     * @return This builder
     */
    public JavaProcessBuilder classpath(final String cp) {
        if(cp == null)
            classpath = System.getProperty("java.class.path");
        else
            classpath = cp;
        return this;
    }
    
    /**
     * Returns this builder's VM arguments. Java subprocesses will use these
     * arguments (in addition to the classpath argument).
     *
     * @return This builder's VM arguments
     */
    public List<String> vmArgs() {
        return vmArgs;
    }
    
    /**
     * Sets this builder's VM arguments. Java subprocesses will use these
     * arguments (in addition to the classpath argument). The list may be
     * <code>null</code> &ndash; this means to use the VM arguments of the
     * current Java process as the VM arguments of the child process.
     *
     * @param vmArguments
     *            The new VM arguments
     * @return This builder
     */
    public JavaProcessBuilder vmArgs(final List<String> vmArguments) {
        if(vmArguments == null) {
            vmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        }
        else
            vmArgs = vmArguments;
        return this;
    }
    
    /* TODO: Support for system properties. From Javadoc: "Note that system
     * properties are generally preferred over environment variables for Java
     * subprocesses. See {@link System#getenv(String)}" */
    
    /**
     * Creates a {@link ProcessBuilder} with a command that reflects the current
     * settings of this builder. Changes subsequently made to this instance are
     * not reflected in the returned process builder.
     * <p>
     * The returned process builder can be further modified, e.g., by setting
     * the working directory, the environment, etc.
     * 
     * @return A process builder
     * @see #javaCommand(String, String, List, String, List)
     */
    public ProcessBuilder create() {
        return new ProcessBuilder(javaCommand(javaHome, classpath, vmArgs,
                mainClass, args));
    }
    
    /**
     * Constructs a {@link ProcessBuilder} command list to start a Java program,
     * corresponding to the provided details.
     * <p>
     * It is recommended to use instances of this class instead of this method.
     * 
     * @param javaHome
     *            The base directory of the Java installation
     * @param classpath
     *            The classpath for the Java program
     * @param vmArgs
     *            The VM arguments (in addition to the classpath)
     * @param mainClass
     *            The name of the main class
     * @param args
     *            The arguments for the main class
     * @return The corresponding command list
     * @throws NullPointerException
     *             If any of the arguments is <code>null</code>
     */
    public static List<String> javaCommand(final String javaHome,
            final String classpath, final List<String> vmArgs,
            final String mainClass, final List<String> args) {
        if(javaHome == null || classpath == null || mainClass == null)
            throw new NullPointerException();
        
        final ArrayList<String> command = new ArrayList<String>();
        command.addAll(asList(javaHome + javaExe, "-cp", classpath));
        command.addAll(vmArgs);
        command.add(mainClass);
        command.addAll(args);
        return command;
    }
}
