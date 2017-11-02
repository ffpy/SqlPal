package entity;

import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.crud.DataSupport;
import com.sqlpal.annotation.TableName;

@TableName(name = "user")
public class User extends DataSupport {
    @PrimaryKey
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
