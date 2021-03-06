package org.sqlpal.sample.crud;

import org.sqlpal.crud.DataSupport;
import org.sqlpal.sample.model.News;
import org.sqlpal.sample.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaveTest {

    /**
     * 存储
     */
    public static void save() {
        // 插入一行
        User user = new User("admin", "123", 18);
        try {
            user.save();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量存储
     */
    public static void saveAll() {
        List<News> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            News news = new News(null, "title_" + i, "content_" + i);
            users.add(news);
        }
        try {
            DataSupport.saveAll(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
