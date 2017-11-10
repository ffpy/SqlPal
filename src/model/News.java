package model;

import com.sqlpal.annotation.AutoIncrement;
import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.annotation.TableName;
import com.sqlpal.crud.DataSupport;

@TableName(name = "news")
public class News extends DataSupport {
    @PrimaryKey
    @AutoIncrement
    private Integer id;
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return id + "\t" + title;
    }
}