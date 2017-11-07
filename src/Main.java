import com.sqlpal.SqlPal;
import com.sqlpal.exception.ConnectionException;
import com.sqlpal.exception.DataSupportException;
import entity.User;

public class Main {

    public static void main(String[] args) {
        SqlPal.init();

        try {
            SqlPal.begin();
            User user = new User();
            user.setUsername("user3");
            user.setPassword("pwd3");
            user.save();
            SqlPal.end();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (DataSupportException e) {
            e.printStackTrace();
        }

        SqlPal.destroy();
    }
}
