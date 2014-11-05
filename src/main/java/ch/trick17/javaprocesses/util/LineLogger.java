package ch.trick17.javaprocesses.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

/**
 * A simple {@link Runnable}/{@link Callable} that forwards all text from a
 * {@link BufferedReader} to a {@link Logger}, line by line. The default logging
 * level is {@linkplain Logger#info(String) INFO}.
 * 
 * @author Michael Faes
 */
public class LineLogger implements Runnable, Callable<Void> {
    
    private final BufferedReader reader;
    private final Logger logger;
    
    private String prefix = "";
    private LogLevel logLevel = LogLevel.INFO;
    
    /**
     * Convenience constructor. The given {@link InputStream} is wrapped in an
     * {@link InputStreamReader} (with the default charset) and in a
     * {@link BufferedReader}.
     * 
     * @param in
     *            Source
     * @param logger
     *            Destination
     */
    public LineLogger(InputStream in, final Logger logger) {
        this(new InputStreamReader(in), logger);
    }
    
    /**
     * Convenience constructor. The given {@link Reader} is wrapped in a
     * {@link BufferedReader}.
     * 
     * @param reader
     *            Source
     * @param logger
     *            Destination
     */
    public LineLogger(final Reader reader, final Logger logger) {
        this(new BufferedReader(reader), logger);
    }
    
    public LineLogger(final BufferedReader reader, final Logger logger) {
        this.reader = reader;
        this.logger = logger;
    }
    
    public void setPrefix(String prefix) {
        if(prefix == null)
            throw new IllegalArgumentException("null");
        this.prefix = prefix;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
    
    public LogLevel getLogLevel() {
        return logLevel;
    }
    
    /**
     * Does the forwarding.
     * 
     * @throws RuntimeException
     *             If an {@link IOException} occurs
     */
    public void run() {
        try {
            call();
        } catch(final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Does the forwarding.
     */
    public Void call() throws IOException {
        String line;
        switch(logLevel) {
        case TRACE:
            while((line = reader.readLine()) != null)
                logger.trace(prefix + line);
            break;
        case DEBUG:
            while((line = reader.readLine()) != null)
                logger.debug(prefix + line);
            break;
        case INFO:
            while((line = reader.readLine()) != null)
                logger.info(prefix + line);
            break;
        case WARN:
            while((line = reader.readLine()) != null)
                logger.warn(prefix + line);
            break;
        case ERROR:
            while((line = reader.readLine()) != null)
                logger.error(prefix + line);
            break;
        }
        return null;
    }
    
    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR;
    }
}
