import com.google.gson.Gson;
import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.annotation.TableName;
import com.sqlpal.crud.DataSupport;

import java.sql.Date;

@TableName(name = "user")
public class User extends DataSupport {
    @PrimaryKey
    private String username;
    private String password;
    private Integer age;
    private Boolean is_boy;
    private Date birth;

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getIs_boy() {
        return is_boy;
    }

    public void setIs_boy(Boolean is_boy) {
        this.is_boy = is_boy;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
