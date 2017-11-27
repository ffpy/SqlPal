package org.sqlpal;

public interface Logger {
    void debug(Object message);

    void info(Object message);

    void warning(Object message);

    void error(Object message);
}
