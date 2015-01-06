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
    private final List<String> vmArgs = new ArrayList<String>(ManagementFactory
            .getRuntimeMXBean().getInputArguments());
    private boolean autoExit = false;
    
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
     * arguments. This constructor does <i>not</i> make a copy of the
     * <code>args</code> array. Subsequent updates to the array will be
     * reflected in the state of this builder (but not in the state of actual
     * process builders).
     * 
     * @param mainClass
     *            The main class
     * @param args
     *            The arguments for the main class
     */
    public JavaProcessBuilder(final Class<?> mainClass, final String... args) {
        this(mainClass, asList(args));
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
            mainMethodOf(mainClass);
        } catch(final NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + name
                    + " does not have a \"public static void main(String[])\""
                    + " method and therefore cannot be started in a process.");
        }
        return name;
    }
    
    public static Method mainMethodOf(final Class<?> mainClass)
            throws NoSuchMethodException {
        final Method main = mainClass.getMethod("main", String[].class);
        final int modifiers = main.getModifiers();
        if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)
                || main.getReturnType() != Void.TYPE)
            throw new NoSuchMethodException("Class " + main.getName()
                    + " does not have a \"public static void main(String[])\""
                    + " method");
        return main;
    }
    
    /**
     * Creates a builder for Java process builders with the given main class and
     * arguments. If the class is in the current classpath, the
     * {@link #JavaProcessBuilder(Class)} constructor should be used instead, as
     * it checks if the class is a valid main class. This constructor does
     * <i>not</i> make a copy of the <code>args</code> array. Subsequent updates
     * to the array will be reflected in the state of this builder (but not in
     * the state of actual process builders).
     * 
     * @param mainClass
     *            The {@linkplain Class#getCanonicalName() canonical name} of
     *            the main class.
     * @param args
     *            The arguments for the main class
     */
    public JavaProcessBuilder(final String mainClass, final String... args) {
        this(mainClass, asList(args));
    }
    
    /**
     * Creates a builder for Java process builders with the given main class and
     * arguments. If the class is in the current classpath, the
     * {@link #JavaProcessBuilder(Class)} constructor should be used instead, as
     * it checks if the class is a valid main class. This constructor does
     * <i>not</i> make a copy of the <code>args</code> list. Subsequent updates
     * to the list will be reflected in the state of this builder (but not in
     * the state of actual process builders).
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
     * Java executable in this directory. If this method is never called, the
     * Java home directory of the current Java process, defined by the system
     * property <code>java.home</code>, is used as the Java home directory of
     * the child process.</p>
     *
     * @param home
     *            The new Java home directory, not null
     * @return This builder
     */
    public JavaProcessBuilder javaHome(final String home) {
        if(home == null)
            throw new NullPointerException();
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
     * classpath. If this method is never called, the classpath of the current
     * Java process, defined by the property <code>java.class.path</code>, is
     * used as the classpath of the child process.</p>
     *
     * @param cp
     *            The new classpath, not null
     * @return This builder
     */
    public JavaProcessBuilder classpath(final String cp) {
        if(cp == null)
            throw new NullPointerException();
        classpath = cp;
        return this;
    }
    
    /**
     * Adds the given string to this builder's classpath.
     *
     * @param cp
     *            The additional classpath element, not null
     * @return This builder
     */
    public JavaProcessBuilder addClasspath(final String cp) {
        if(cp == null)
            throw new NullPointerException();
        
        if(classpath.isEmpty())
            classpath = cp;
        else
            classpath += File.separator + cp;
        return this;
    }
    
    /**
     * Returns this builder's VM arguments. Java subprocesses will use these
     * arguments (in addition to the classpath argument).
     *
     * @return This builder's VM arguments
     */
    public List<String> vmArgs() {
        return new ArrayList<String>(vmArgs);
    }
    
    /**
     * Sets this builder's VM arguments. Java subprocesses will use these
     * arguments (in addition to the classpath argument). If this method is
     * never called, the VM arguments of the current Java process are used as
     * the VM arguments of the child process.
     *
     * @param vmArguments
     *            The new VM arguments (not null)
     * @return This builder
     */
    public JavaProcessBuilder vmArgs(final String... vmArguments) {
        if(vmArguments == null)
            throw new NullPointerException();
        vmArgs.clear();
        vmArgs.addAll(asList(vmArguments));
        return this;
    }
    
    /**
     * Adds the given string to this builder's VM arguments.
     *
     * @param vmArguments
     *            The additional VM arguments, not null
     * @return This builder
     */
    public JavaProcessBuilder addVmArgs(final String... vmArguments) {
        if(vmArguments == null)
            throw new NullPointerException();
        vmArgs.addAll(asList(vmArguments));
        return this;
    }
    
    /**
     * Returns this builder's auto exit flag. If <code>true</code>, Java
     * subprocesses will be started via {@link AutoExitProgram}.
     *
     * @return This builder's auto exit flag
     * @see AutoExit
     */
    public boolean autoExit() {
        return autoExit;
    }
    
    /**
     * Sets this builder's auto exit flag. If <code>true</code>, Java
     * subprocesses will be started via {@link AutoExitProgram}.
     *
     * @param exit
     *            The new value for the auto exit flag
     * @return This builder
     */
    public JavaProcessBuilder autoExit(final boolean exit) {
        autoExit = exit;
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
     */
    public ProcessBuilder create() {
        return new ProcessBuilder(javaCommand());
    }
    
    /**
     * Convenience method that {@linkplain #create() creates} a process builder
     * and {@linkplain ProcessBuilder#start() starts} a process.
     * 
     * @return The started process
     * @throws IOException
     *             If one is thrown by the {@link ProcessBuilder#start()} method
     */
    public Process start() throws IOException {
        return create().start();
    }
    
    /**
     * Constructs the {@link ProcessBuilder} command list to start a Java
     * program, corresponding to the current configuration of this builder.
     * 
     * @return The command list
     */
    public List<String> javaCommand() {
        final List<String> command = new ArrayList<String>();
        command.addAll(asList(javaHome + javaExe, "-cp", classpath));
        command.addAll(vmArgs);
        if(autoExit)
            command.add(AutoExitProgram.class.getName());
        command.add(mainClass);
        command.addAll(args);
        return command;
    }
}
