package net.togogo.newsclient.bean;

import java.util.List;

/**
 * Created by Lam on 2017/9/16.
 */

public class NewsBean {
    int code;
    String msg;
    List<NewsContent> newslist;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public List<NewsContent> getNewslist() {
        return newslist;
    }

    @Override
    public String toString() {
        return "NewsBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", newslist=" + newslist +
                '}';
    }
}
