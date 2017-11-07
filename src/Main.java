import com.sqlpal.SqlPal;
import com.sqlpal.exception.ConnectionException;
import entity.News;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        SqlPal.init();

        try {
            SqlPal.begin();

            News news = new News();
            news.setId(816);
            news.setTitle("aaa");
            news.save();

            SqlPal.end();
        } catch (SQLException | ConnectionException e) {
            e.printStackTrace();
        }

        SqlPal.destroy();
    }
}
