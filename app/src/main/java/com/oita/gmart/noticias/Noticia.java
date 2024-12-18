package com.oita.gmart.noticias;

/**
 * Created by gmart on 08/01/2018.
 */

public class Noticia {
    private String title;
    private String date;
    private String content;
    private String thumbnailUrl;

    public Noticia(String title, String date, String content, String thumbnailUrl) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
