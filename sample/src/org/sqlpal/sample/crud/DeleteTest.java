package org.sqlpal.sample.crud;

import org.sqlpal.crud.DataSupport;
import org.sqlpal.sample.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeleteTest {

    /**
     * 删除
     */
    public static void delete() {
        // 删除主键为user1的行
        User user = new User();
        user.setUsername("user1");
        try {
            user.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量删除
     */
    public static void deleteAll() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setUsername("user" + i);
            users.add(user);
        }
        try {
            DataSupport.deleteAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 约束删除
     */
    public static void deleteByConditions() {
        // 删除age大于10的行
        try {
            DataSupport.deleteAll(User.class, "age > ?", "10");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
