import com.sqlpal.SqlPal;
import com.sqlpal.crud.DataSupport;
import entity.News;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        SqlPal.init();

        try {

            Statement stmt = DataSupport.executeQuery("select * from news");
            ResultSet rs = stmt.getResultSet();
            while (rs.next()) {
                System.out.println(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            SqlPal.end();
        }


        SqlPal.destroy();
    }
}
