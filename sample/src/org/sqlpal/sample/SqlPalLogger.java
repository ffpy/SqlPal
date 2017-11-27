package org.sqlpal.sample;

import org.sqlpal.Logger;

public class SqlPalLogger implements Logger {

    @Override
    public void debug(Object message) {
        System.out.println("debug: " + message);
    }

    @Override
    public void info(Object message) {
        System.out.println("info: " + message);
    }

    @Override
    public void warning(Object message) {
        System.out.println("warning: " + message);
    }

    @Override
    public void error(Object message) {
        System.out.println("error: " + message);
    }
}
