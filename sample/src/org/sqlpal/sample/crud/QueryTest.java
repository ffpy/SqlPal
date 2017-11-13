package org.sqlpal.sample.crud;

import com.google.gson.Gson;
import org.sqlpal.SqlPal;
import org.sqlpal.crud.DataSupport;
import org.sqlpal.sample.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class QueryTest {

    /**
     * 查找所有
     */
    public static void findAll() {
        try {
            List<User> users = DataSupport.findAll(User.class);
            for (User user : users) {
                System.out.println(new Gson().toJson(user));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连缀查找
     */
    public static void find() {
        try {
            List<User> users = DataSupport.where("age > ?", "15").select("username, password").limit(2).offset(1).find(User.class);
            for (User user : users) {
                System.out.println(new Gson().toJson(user));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 原生查询
     */
    public static void executeQuery() {
        try {
            SqlPal.begin();
            try {
                Statement stmt = DataSupport.executeQuery("select * from user");
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    System.out.print(rs.getString("username") + " ");
                    System.out.print(rs.getString("password") + " ");
                    System.out.println(rs.getInt("age"));
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                SqlPal.end();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计行数
     */
    public static void count() {
        try {
            int count = DataSupport.where("age > ?", "16").count(User.class);
            System.out.println(count);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计总和
     */
    public static void sum() {
        try {
            int sum = DataSupport.where("age > ?", "16").sum(User.class, "age", int.class);
            System.out.println(sum);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计平均值
     */
    public static void average() {
        try {
            double average = DataSupport.where("age > ?", "16").average(User.class, "age");
            System.out.println(average);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计最大值
     */
    public static void max() {
        try {
            int max = DataSupport.max(User.class, "age", int.class);
            System.out.println(max);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计最小值
     */
    public static void min() {
        try {
            int min = DataSupport.min(User.class, "age", int.class);
            System.out.println(min);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
