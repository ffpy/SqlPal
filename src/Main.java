import com.sqlpal.SqlPal;
import com.sqlpal.crud.DataSupport;
import com.sqlpal.exception.DataSupportException;
import entity.User;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            SqlPal.init();

            long t = System.currentTimeMillis();
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                User user = new User();
                user.setUsername("user" + i);
                user.setPassword("pwd" + i + "-updated");
//                user.save();
                users.add(user);
            }

            DataSupport.updateAll(users);

            t = System.currentTimeMillis() - t;
            System.out.println(t + "ms");

            SqlPal.destroy();
        } catch (DataSupportException e) {
            e.printStackTrace();
        }
    }
}
