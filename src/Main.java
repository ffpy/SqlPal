import com.sqlpal.SqlPal;
import com.sqlpal.crud.DataSupport;
import com.sqlpal.exception.DataSupportException;
import entity.User;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            SqlPal.init();

//            User user = new User();
//            user.setUsername("小明");
//            user.setPassword("123");
//            user.setAge(18);
//            user.save();

//            for (int i = 0; i < 10; i++) {
//                char ch = (char) ('a' + i);
//                String username = "" + ch + ch + ch;
//                String password = "" + i + i + i;
//                User user = new User();
//                user.setUsername(username);
//                user.setPassword(password);
//                user.save();
//            }

            List<User> list = DataSupport.limit(3).find(User.class);
//            List<User> list = DataSupport.findAll(User.class);
            for (User user : list) {
                System.out.println(user.getUsername() + " " + user.getPassword() + " " + user.getAge());
            }

            SqlPal.destroy();
        } catch (DataSupportException e) {
            e.printStackTrace();
        }
    }
}
