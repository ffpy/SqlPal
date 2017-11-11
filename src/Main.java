import com.sqlpal.SqlPal;
import com.sqlpal.crud.DataSupport;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

public class Main {

    public static void main(String[] args) throws SQLException {
        SqlPal.init("sqlpal.xml");

        User user = DataSupport.findFirst(User.class);
        System.out.println(user);

//        User user = new User();
//        user.setUsername("user1");
//        user.setPassword("pwd1");
//        user.setAge(20);
//        user.setIs_boy(false);
//        user.setBirth(new Date(Calendar.getInstance().getTimeInMillis()));
//        user.save();

        SqlPal.destroy();
    }
}
