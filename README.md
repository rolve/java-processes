# java-processes

This is a small library to help you create and (automatically kill) Java processes from
within Java. This is useful in a number of situations:

* Some part of your program needs different JVM args, a different classpath, or even
  a different JVM.
* You want to safely execute code that may run out of memory.
* You want to execute multiple instances of legacy code with global state in parallel.

To start part of your Java program in a separate JVM, simply write:

```java
Process proc = new JavaProcessBuilder(SomeClass.class).start();
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
        .directory(new File("test")
        .inheritIO()
        .start();
```

What is happening here is that `.build()` returns a `ProcessBuilder` instance that
has all the JVM-specific configuration imprinted and can then be further configured.
In fact, the `JavaProcessBuilder.start()` method is simply a shorthand for
`build().start()`.
