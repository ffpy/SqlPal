package entity;

import com.sqlpal.annotation.PrimaryKey;
import com.sqlpal.annotation.TableName;
import com.sqlpal.crud.DataSupport;

@TableName(name = "news_comment")
public class NewsComment extends DataSupport {
    @PrimaryKey private Integer id;
    private String content;
    private News news;

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

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }
}
