package org.sqlpal.connection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Connection代理类
 * 在调用close方法的时候自动关闭所有创建的statement
 */
public class ConnectionProxy implements InvocationHandler {
    private static ClassLoader mClassLoader;
    private static Class<?>[] mInterfaces;
    private Connection mConnection;
    private List<Statement> mStatements = new ArrayList<>();

    public static Connection instance(Connection connection) {
        if (mClassLoader == null || mInterfaces == null) {
            Class<?> cls = connection.getClass();
            mClassLoader = cls.getClassLoader();
            mInterfaces = cls.getInterfaces();
        }
        return (Connection) Proxy.newProxyInstance(mClassLoader, mInterfaces, new ConnectionProxy(connection));
    }

    private ConnectionProxy(Connection connection) {
        this.mConnection = connection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("close".equals(method.getName())) closeStatements();
        Object res = method.invoke(mConnection, args);
        if (res instanceof Statement) mStatements.add((Statement) res);
        return res;
    }

    /**
     * 关闭所有Statement
     */
    private void closeStatements() {
        for (Statement statement : mStatements) {
            try {
                statement.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
