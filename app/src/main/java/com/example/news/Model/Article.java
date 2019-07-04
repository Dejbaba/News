package com.example.news.Model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


public class Article {


    private int id;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urltoimage;
    private String publishedat;



    public Article(int id, String author, String title, String description, String url,
                   String urltoimage, String publishedat) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urltoimage = urltoimage;
        this.publishedat = publishedat;
    }

    public Article(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrltoimage() {
        return urltoimage;
    }

    public void setUrltoimage(String urltoimage) {
        this.urltoimage = urltoimage;
    }

    public String getPublishedat() {
        return publishedat;
    }

    public void setPublishedat(String publishedat) {
        this.publishedat = publishedat;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", urltoimage='" + urltoimage + '\'' +
                ", publishedat='" + publishedat + '\'' +
                '}';
    }
}
