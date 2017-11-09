import com.sqlpal.SqlPal;
import com.sqlpal.exception.ConnectionException;
import entity.*;

import java.sql.SQLException;

public class Main {
    private static final int batchNum = 100;

    public static void main(String[] args) {
        SqlPal.init();

        new MyThread1().start();
        new MyThread2().start();
        new MyThread3().start();
        new MyThread4().start();

//        SqlPal.destroy();
    }

    static class MyThread1 extends Thread {

        @Override
        public void run() {
            Thread.currentThread().setName("thread1");
            try {
                for (int i = 1; i <= batchNum; i++) {
                    T1 model = new T1();
                    model.setContent("thread1-" + i);
                    model.save();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static class MyThread2 extends Thread {

        @Override
        public void run() {
            Thread.currentThread().setName("thread2");
            try {
                for (int i = 1; i <= batchNum; i++) {
                    T2 model = new T2();
                    model.setContent("thread2-" + i);
                    model.save();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static class MyThread3 extends Thread {

        @Override
        public void run() {
            Thread.currentThread().setName("thread3");
            try {
                for (int i = 1; i <= batchNum; i++) {
                    T3 model = new T3();
                    model.setContent("thread3-" + i);
                    model.save();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static class MyThread4 extends Thread {

        @Override
        public void run() {
            Thread.currentThread().setName("thread4");
            try {
                for (int i = 1; i <= batchNum; i++) {
                    T4 model = new T4();
                    model.setContent("thread4-" + i);
                    model.save();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
