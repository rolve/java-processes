package ch.trick17.javaprocesses;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to create Java processes.
 * <p>
 * This class provides a simple way to start other Java programs. By default,
 * the Java executable, the classpath, the working directory and the environment
 * are the same as for the current Java process, but can be changed if desired.
 * In addition, this class provides a simple way to automatically kill
 * subprocesses when the current JVM shuts down.
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
    
    private static final Logger logger = LoggerFactory
            .getLogger(JavaProcessBuilder.class);
    
    private static final String javaExe = File.separator + "bin"
            + File.separator + "java";
    
    private final String mainClass;
    private final List<String> args;
    
    private String javaHome = System.getProperty("java.home");
    private String classpath = System.getProperty("java.class.path");
    private List<String> vmArgs = ManagementFactory.getRuntimeMXBean()
            .getInputArguments();
    private boolean killOnShutdown = false;
    
    private final ProcessBuilder builder = new ProcessBuilder();
    
    /**
     * Creates a builder that starts a new Java processes with the given main
     * class and an empty argument list.
     * 
     * @param mainClass
     *            The main class
     */
    public JavaProcessBuilder(final Class<?> mainClass) {
        this(mainClass, Collections.<String> emptyList());
    }
    
    /**
     * Creates a builder that starts a new Java processes with the given main
     * class and arguments. This is a convenience constructor that sets the
     * process builder's command to a string list containing the same strings as
     * the command array, in the same order.
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
     * Creates a builder that starts a new Java processes with the given main
     * class and arguments. This constructor does <i>not</i> make a copy of the
     * <code>args</code> list. Subsequent updates to the list will be reflected
     * in the state of the process builder.
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
     * Creates a builder that starts a new Java processes with the given main
     * class and arguments. If the class is in the current classpath, the
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
     * Returns this process builder's Java home directory. Java subprocesses
     * subsequently started by this object's {@link #start()} method will use
     * the Java executable in this directory.
     *
     * @return This process builder's Java home directory
     */
    public String javaHome() {
        return javaHome;
    }
    
    /**
     * Sets this process builder's Java home directory. Java subprocesses
     * subsequently started by this object's {@link #start()} method will use
     * the Java executable in this directory. The argument may be
     * <code>null</code> &ndash; this means to use the Java home directory of
     * the current Java process, defined by the system property
     * <code>java.home</code>, as the Java home directory of the child
     * process.</p>
     *
     * @param home
     *            The new Java home directory
     * @return This process builder
     */
    public JavaProcessBuilder javaHome(final String home) {
        if(home == null)
            javaHome = System.getProperty("java.home");
        else
            javaHome = home;
        return this;
    }
    
    /**
     * Returns this process builder's classpath. Java subprocesses subsequently
     * started by this object's {@link #start()} method will use this as their
     * classpath.
     *
     * @return This process builder's classpath
     */
    public String classpath() {
        return classpath;
    }
    
    /**
     * Sets this process builder's classpath. Java subprocesses subsequently
     * started by this object's {@link #start()} method will use this as their
     * classpath. The argument may be <code>null</code> &ndash; this means to
     * use the classpath of the current Java process, defined by the system
     * property <code>java.class.path</code>, as the classpath of the child
     * process.</p>
     *
     * @param cp
     *            The new classpath
     * @return This process builder
     */
    public JavaProcessBuilder classpath(final String cp) {
        if(cp == null)
            classpath = System.getProperty("java.class.path");
        else
            classpath = cp;
        return this;
    }
    
    /**
     * Returns this process builder's VM arguments. Java subprocesses
     * subsequently started by this object's {@link #start()} method will use
     * these arguments in addition to the classpath argument.
     *
     * @return This process builder's VM arguments
     */
    public List<String> vmArgs() {
        return vmArgs;
    }
    
    /**
     * Sets this process builder's VM arguments. Java subprocesses subsequently
     * started by this object's {@link #start()} method will use these arguments
     * in addition to the classpath argument. The list may be <code>null</code>
     * &ndash; this means to use the VM arguments of the current Java process as
     * the VM arguments of the child process.
     *
     * @param vmArguments
     *            The new VM arguments
     * @return This process builder
     */
    public JavaProcessBuilder vmArgs(final List<String> vmArguments) {
        if(vmArguments == null) {
            vmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        }
        else
            vmArgs = vmArguments;
        return this;
    }
    
    /**
     * Tells whether this process builder creates Java processes that are killed
     * when this VM shuts down.
     * <p>
     * If this property is <code>true</code>, then Java subprocesses are killed
     * when this VM shuts down. This is accomplished using
     * {@linkplain Runtime#addShutdownHook(Thread) shutdown hooks}. The initial
     * value is <code>false</code>.
     *
     * @return This process builder's <code>killOnShutdow</code> property
     */
    public boolean killOnShutdown() {
        return killOnShutdown;
    }
    
    /**
     * Sets this process builder's <code>killOnShutdown</code> property.
     * <p>
     * If this property is <code>true</code>, then Java subprocesses are killed
     * when this VM shuts down. This is accomplished using
     * {@linkplain Runtime#addShutdownHook(Thread) shutdown hooks}. The initial
     * value is <code>false</code>.
     *
     * @param kill
     *            The new property value
     * @return This process builder
     */
    public JavaProcessBuilder killOnShutdown(final boolean kill) {
        killOnShutdown = kill;
        return this;
    }
    
    /**
     * Returns this process builder's working directory. Java subprocesses
     * subsequently started by this object's {@link #start()} method will use
     * this as their working directory. The returned value may be
     * <code>null</code> &ndash; this means to use the working directory of the
     * current Java process, usually the directory named by the system property
     * <code>user.dir</code>, as the working directory of the child process.
     *
     * @return This process builder's working directory
     */
    public File directory() {
        return builder.directory();
    }
    
    /**
     * Sets this process builder's working directory. Java subprocesses
     * subsequently started by this object's {@link #start()} method will use
     * this as their working directory. The argument may be <code>null</code>
     * &ndash; this means to use the working directory of the current Java
     * process, usually the directory named by the system property
     * <code>user.dir</code>, as the working directory of the child process.
     *
     * @param directory
     *            The new working directory
     * @return This process builder
     */
    public JavaProcessBuilder directory(final File directory) {
        builder.directory(directory);
        return this;
    }
    
    /**
     * Returns a string map view of this process builder's environment. Whenever
     * a process builder is created, the environment is initialized to a copy of
     * the current process environment (see {@link System#getenv()}). Java
     * subprocesses subsequently started by this object's {@link #start()}
     * method will use this map as their environment.
     * <p>
     * The returned object may be modified using ordinary {@link java.util.Map
     * Map} operations. These modifications will be visible to subprocesses
     * started via the {@link #start()} method. Two <code>ProcessBuilder</code>
     * instances always contain independent process environments, so changes to
     * the returned map will never be reflected in any other
     * <code>ProcessBuilder</code> instance or the values returned by
     * {@link System#getenv System.getenv}.
     * <p>
     * If the system does not support environment variables, an empty map is
     * returned.
     * <p>
     * The returned map does not permit null keys or values. Attempting to
     * insert or query the presence of a null key or value will throw a
     * {@link NullPointerException}. Attempting to query the presence of a key
     * or value which is not of type {@link String} will throw a
     * {@link ClassCastException}.
     * <p>
     * The behavior of the returned map is system-dependent. A system may not
     * allow modifications to environment variables or may forbid certain
     * variable names or values. For this reason, attempts to modify the map may
     * fail with {@link UnsupportedOperationException} or
     * {@link IllegalArgumentException} if the modification is not permitted by
     * the operating system.
     * <p>
     * Since the external format of environment variable names and values is
     * system-dependent, there may not be a one-to-one mapping between them and
     * Java's Unicode strings. Nevertheless, the map is implemented in such a
     * way that environment variables which are not modified by Java code will
     * have an unmodified native representation in the subprocess.
     * <p>
     * The returned map and its collection views may not obey the general
     * contract of the {@link Object#equals} and {@link Object#hashCode}
     * methods.
     * <p>
     * The returned map is typically case-sensitive on all platforms.
     * <p>
     * If a security manager exists, its {@link SecurityManager#checkPermission
     * checkPermission} method is called with a
     * <code>{@link RuntimePermission}("getenv.*")</code> permission. This may
     * result in a {@link SecurityException} being thrown.
     *
     * @return This process builder's environment
     * @throws SecurityException
     *             If a security manager exists and its
     *             {@link SecurityManager#checkPermission checkPermission}
     *             method doesn't allow access to the process environment
     */
    public Map<String, String> environment() {
        return builder.environment();
    }
    
    /* TODO: Support for system properties. From Javadoc: "Note that system
     * properties are generally preferred over environment variables for Java
     * subprocesses. See {@link System#getenv(String)}" */
    
    /**
     * Tells whether this process builder merges standard error and standard
     * output.
     * <p>
     * If this property is <code>true</code>, then any error output generated by
     * subprocesses subsequently started by this object's {@link #start()}
     * method will be merged with the standard output, so that both can be read
     * using the {@link Process#getInputStream()} method. This makes it easier
     * to correlate error messages with the corresponding output. The initial
     * value is <code>false</code>.
     * </p>
     *
     * @return This process builder's <code>redirectErrorStream</code> property
     */
    public boolean redirectErrorStream() {
        return builder.redirectErrorStream();
    }
    
    /**
     * Sets this process builder's <code>redirectErrorStream</code> property.
     * <p>
     * If this property is <code>true</code>, then any error output generated by
     * subprocesses subsequently started by this object's {@link #start()}
     * method will be merged with the standard output, so that both can be read
     * using the {@link Process#getInputStream()} method. This makes it easier
     * to correlate error messages with the corresponding output. The initial
     * value is <code>false</code>.
     * </p>
     *
     * @param redirectErrorStream
     *            The new property value
     * @return This process builder
     */
    public JavaProcessBuilder redirectErrorStream(
            final boolean redirectErrorStream) {
        builder.redirectErrorStream(redirectErrorStream);
        return this;
    }
    
    /**
     * Starts a new Java process using the attributes of this process builder.
     * <p>
     * If there is a security manager, its {@link SecurityManager#checkExec
     * checkExec} method is called with the Java executable as its argument.
     * This may result in a {@link SecurityException} being thrown.
     * <p>
     * Starting an operating system process is highly system-dependent. Among
     * the many things that can go wrong are:
     * <ul>
     * <li>The Java executable file was not found.
     * <li>Access to the Java executable file was denied.
     * <li>The working directory does not exist.
     * </ul>
     * <p>
     * In such cases an exception will be thrown. The exact nature of the
     * exception is system-dependent, but it will always be a subclass of
     * {@link IOException}.
     * <p>
     * Subsequent modifications to this process builder will not affect the
     * returned {@link Process}.
     * </p>
     *
     * @return A new {@link Process} object for managing the subprocess
     * @throws NullPointerException
     *             If an element of the argument list is null
     * @throws SecurityException
     *             If a security manager exists and its
     *             {@link SecurityManager#checkExec checkExec} method doesn't
     *             allow creation of the subprocess
     * @throws IOException
     *             If an I/O error occurs
     * @see SecurityManager#checkExec(String)
     */
    public Process start() throws IOException {
        final ArrayList<String> command = new ArrayList<String>();
        command.addAll(asList(javaHome + javaExe, "-cp", classpath));
        command.addAll(vmArgs);
        command.add(mainClass);
        command.addAll(args);
        
        logger.debug("Starting Java process: {}", command);
        return builder.command(command).start();
        
        // TODO: Kill
    }
}
