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



            SqlPal.destroy();
        } catch (DataSupportException e) {
            e.printStackTrace();
        }
    }
}
