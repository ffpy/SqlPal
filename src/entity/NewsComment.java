package entity;

import com.sqlpal.annotation.AutoIncrement;
import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.annotation.TableName;
import com.sqlpal.crud.DataSupport;

@TableName(name = "news_comment")
public class NewsComment extends DataSupport {
    @PrimaryKey
    @AutoIncrement
    private Integer id;
    private String content;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
