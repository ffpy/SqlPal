import com.sqlpal.SqlPal;
import model.News;
import model.User;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        // 初始化
        SqlPal.init("sqlpal.xml");

        try {
            SqlPal.begin();

            try {
                // 关闭自动提交
                SqlPal.setAutoCommit(false);

                News news = new News();
                news.setTitle("title");
                news.setContent("content");
                news.save();

                User user = new User();
                user.setUsername("admin");
                user.setPassword("123");
                user.save();

                // 提交事务
                SqlPal.commit();
            } catch (SQLException e) {
                SqlPal.rollback();
                e.printStackTrace();
            } finally {
                SqlPal.end();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // 销毁
        SqlPal.destroy();
    }
}
