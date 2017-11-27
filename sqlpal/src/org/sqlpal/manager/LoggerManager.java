package org.sqlpal.manager;

import org.sqlpal.Logger;

/**
 * Logger管理器
 */
public class LoggerManager {
    public static int LEVEL_ALL = 0;
    public static int LEVEL_DEBUG = 1;
    public static int LEVEL_INFO = 2;
    public static int LEVEL_WARNING = 3;
    public static int LEVEL_ERROR = 4;
    public static int LEVEL_NONE = 5;

    private static Logger mLogger;
    private static int mLevel = LEVEL_ALL;

    public static void init(Logger logger) {
        mLogger = logger;
    }

    public static Logger getLogger() {
        return mLogger;
    }

    public static void setLogger(Logger logger) {
        LoggerManager.mLogger = logger;
    }

    public static void debug(Object message) {
        if (mLogger != null && mLevel <= LEVEL_DEBUG) {
            mLogger.debug(message);
        }
    }

    public static void info(Object message) {
        if (mLogger != null && mLevel <= LEVEL_INFO) {
            mLogger.info(message);
        }
    }

    public static void warning(Object message) {
        if (mLogger != null && mLevel <= LEVEL_WARNING) {
            mLogger.warning(message);
        }
    }

    public static void error(Object message) {
        if (mLogger != null && mLevel <= LEVEL_ERROR) {
            mLogger.error(message);
        }
    }

    public static void setLevel(int level) {
        mLevel = level;
    }

    public static int getLevel() {
        return mLevel;
    }

    public static void destroy() {
        mLogger = null;
    }
}
