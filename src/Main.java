import com.sqlpal.SqlPal;
import com.sqlpal.crud.DataSupport;
import com.sqlpal.exception.DataSupportException;
import entity.User;

public class Main {

    public static void main(String[] args) {
        try {
            SqlPal.init();

            double n = DataSupport.average(User.class, "username");
            System.out.println(n);

            SqlPal.destroy();
        } catch (DataSupportException e) {
            e.printStackTrace();
        }
    }
}
