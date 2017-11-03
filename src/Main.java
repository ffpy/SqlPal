import com.sqlpal.SqlPal;
import com.sqlpal.exception.SqlPalException;
import entity.User;

public class Main {

    public static void main(String[] args) {
        try {
            SqlPal.init();
            SqlPal.beginTransaction();
            User user = new User();
            user.setUsername("bbb");
            user.setPassword("123");
            user.update();
            SqlPal.endTransaction();
        } catch (SqlPalException e) {
            e.printStackTrace();
        }
    }
}
