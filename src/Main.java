import com.sqlpal.SqlPal;
import com.sqlpal.exception.DataSupportException;
import entity.User;

public class Main {

    public static void main(String[] args) {
        try {
            SqlPal.init();

            User user = new User();
            user.setUsername("abc");
            user.setPassword("123");
            user.save();

            SqlPal.destroy();
        } catch (DataSupportException e) {
            e.printStackTrace();
        }
    }
}
