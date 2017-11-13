package org.sqlpal.sample.crud;

import org.sqlpal.crud.DataSupport;
import org.sqlpal.sample.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateTest {

    /**
     * 更新模型类
     */
    public static void update() {
        // 将用户名为admin的密码更改为456
        User user = new User();
        user.setUsername("admin");
        user.setPassword("456");
        try {
            user.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量更新
     */
    public static void updateAll() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setAge(20 + i);
            users.add(user);
        }
        try {
            DataSupport.updateAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询更新
     */
    public static void updateByCondictions() {
        // 将age大于10的所有行的密码修改为aaa
        User user = new User();
        user.setPassword("aaa");
        try {
            user.updateAll("age > ?", "10");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
