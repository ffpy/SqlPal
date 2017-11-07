import com.sqlpal.SqlPal;
import com.sqlpal.crud.Cursor;
import com.sqlpal.crud.DataSupport;
import com.sqlpal.exception.ConnectionException;
import com.sqlpal.exception.DataSupportException;
import entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SqlPal.init();

        try {
            SqlPal.begin();
            int n = DataSupport.executeUpdate("delete from user where username = ?", "user3");
            System.out.println(n);
            SqlPal.end();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (DataSupportException e) {
            e.printStackTrace();
        }

        SqlPal.destroy();
    }
}
