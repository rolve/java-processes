package ch.trick17.javaprocesses.util;

import java.io.BufferedReader;
import java.util.concurrent.Callable;

import org.slf4j.Logger;

/**
 * A simple {@link Runnable}/{@link Callable} that forwards all text from a
 * {@link BufferedReader} to a {@link Logger}, line by line. The default logging
 * level is {@linkplain Logger#info(String) INFO}.
 * 
 * @author Michael Faes
 */
public class LineLogger implements LineWriter {
    
    private final Logger logger;
    
    private String prefix = "";
    private LogLevel logLevel = LogLevel.INFO;
    
    public LineLogger(Logger logger) {
        this.logger = logger;
    }
    
    public LineLogger withPrefix(String thePrefix) {
        if(thePrefix == null)
            throw new IllegalArgumentException("null");
        this.prefix = thePrefix;
        return this;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public LineLogger withLogLevel(LogLevel level) {
        this.logLevel = level;
        return this;
    }
    
    public LogLevel getLogLevel() {
        return logLevel;
    }
    
    public void writeLine(String line) {
        switch(logLevel) {
        case TRACE:
            logger.trace(prefix + line);
            break;
        case DEBUG:
            logger.debug(prefix + line);
            break;
        case INFO:
            logger.info(prefix + line);
            break;
        case WARN:
            logger.warn(prefix + line);
            break;
        case ERROR:
            logger.error(prefix + line);
            break;
        }
    }
    
    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR;
    }
}
