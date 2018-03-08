# java-processes

This is a small library to help you create and (automatically kill) Java processes from
within Java. This is useful in a number of situations:

* Some part of your program needs different JVM args, a different classpath, or even
  a different JVM.
* You want to safely execute code that may run out of memory.
* You want to execute multiple instances of legacy code with global state in parallel.

To start part of your Java program in a separate JVM, simply write:

```java
Process proc = new JavaProcessBuilder(SomeClass.class, "some", "args").start();
```

That's it! This snippet will start a new JVM with `SomeClass` as the main class. No
need to specify the classpath, the VM args, or the path to the Java executable; these
are all taken from the JVM in which your program is running!

## Configuration

`JavaProcessBuilder` makes it very simple to configure the classpath, the VM args, or
the Java executable, if you want them to be different from those of the parent JVM:

```java
Process proc = new JavaProcessBuilder(SomeClass.class)
        .javaHome("/my/own/java/")
        .classpath("some-classes.jar")
        .vmArgs("-ea", "-Xmx8g")
        .start();
```

If you know the [ProcessBuilder](https://docs.oracle.com/javase/9/docs/api/java/lang/ProcessBuilder.html)
class from the Java Standard Library, you may also know that lets you specify a number
of additional options when starting a process, such as environment variables, working
directory, or I/O behavior. `JavaProcessBuilder` is actually built on top of
`ProcessBuilder`, so you can configure all of these things too:

```java
Process proc = new JavaProcessBuilder(SomeClass.class)
        .classpath("some-classes.jar")
        .build()
        .directory(new File("test"))
        .inheritIO()
        .start();
```

What is happening here is that `.build()` returns a `ProcessBuilder` instance that
has all the JVM-specific configuration imprinted and can then be further configured.
In fact, the `JavaProcessBuilder.start()` method is simply a shorthand for
`build().start()`.

## Auto-Exit

One thing that is annoying when executing parts of your program in separate JVMs is
that they continue to run when you kill the parent process (especially during
debugging). This library comes with *two* mechanism that can help you with that.

The first mechanism is based on
[shutdown hooks](https://docs.oracle.com/javase/9/docs/api/java/lang/Runtime.html#addShutdownHook-java.lang.Thread-)
and can be used in the following way:

```java
new AutoProcessKiller().add(process);
```

Now, when the JVM shuts down, the `AutoProcessKiller` (which is a Thread) is executed
and kills the process (if it is still running).
However, this will not work if the JVM is terminated forcibly, which happens, for
example, if you press the stop button in Eclipse. In this case, the shutdown hooks
may not be executed, so the child processes may continue to run. In fact, I think
*any* mechanism to kill child processes from within the parent JVM is doomed to
suffer from this problem.

This is why this library comes with a second mechanism, which works from within the
*child* VM and is guaranteed to kill the child process as soon as the parent process
stops running (for whatever reason). You can use it by calling

```java
AutoExit.install();
```

in the child VM. Or, to make it even simpler, you can call `autoExit(true)` on the
process builder instance that you use to start the child VM:

```java
Process proc = new JavaProcessBuilder(SomeClass.class).autoExit(true).start();
```

In this case, the process builder will use the `AutoExitProgram` class as the actual
main class, which in turn calls `AutoExit.install()` and then calls `SomeClass.main()`.
