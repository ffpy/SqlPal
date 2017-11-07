import com.sqlpal.SqlPal;
import com.sqlpal.crud.DataSupport;
import com.sqlpal.exception.ConnectionException;
import com.sqlpal.exception.DataSupportException;
import entity.News;
import entity.User;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SqlPal.init();

        try {
            SqlPal.begin();

            List<News> newsArrayList = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                News news = new News();
                news.setTitle("test");
                newsArrayList.add(news);
            }
            DataSupport.saveAll(newsArrayList);

            for (News n : newsArrayList) {
                System.out.println(n.getId());
            }

            SqlPal.end();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (DataSupportException e) {
            e.printStackTrace();
        }

        SqlPal.destroy();
    }
}
