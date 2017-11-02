import com.sqlpal.SqlPal;
import com.sqlpal.exception.SqlPalException;
import entity.User;

public class Main {

    public static void main(String[] args) {
        try {
            SqlPal.init();
            SqlPal.begin();
            User user = new User();
            user.setUsername("aaa");
            user.setPassword("123");
            user.delete();
            SqlPal.end();
        } catch (SqlPalException e) {
            e.printStackTrace();
        }
    }
}
