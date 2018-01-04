package net.togogo.newsclient.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Lam on 2017/9/16.
 */
@Entity
public class NewsContent {
    @Id(autoincrement = true)
    Long  id;
    String ctime;
    String title;
    String description;
    String picUrl;
    String url;
    String newsType;
    @Generated(hash = 686368591)
    public NewsContent(Long id, String ctime, String title, String description,
            String picUrl, String url, String newsType) {
        this.id = id;
        this.ctime = ctime;
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.url = url;
        this.newsType = newsType;
    }
    @Generated(hash = 1577047943)
    public NewsContent() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCtime() {
        return this.ctime;
    }
    public void setCtime(String ctime) {
        this.ctime = ctime;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getPicUrl() {
        return this.picUrl;
    }
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getNewsType() {
        return this.newsType;
    }
    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }


   
}
