package entity;

import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.annotation.TableName;
import com.sqlpal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

@TableName(name = "news")
public class News extends DataSupport {
    @PrimaryKey private Integer id;
    private String title;
    private NewsDetail detail;
    private List<NewsComment> comments = new ArrayList<>();

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

    public NewsDetail getDetail() {
        return detail;
    }

    public void setDetail(NewsDetail detail) {
        this.detail = detail;
    }

    public List<NewsComment> getComments() {
        return comments;
    }

    public void setComments(List<NewsComment> comments) {
        this.comments = comments;
    }
}
