package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Created by Administrator on 2015/11/13.
 */
@Document(collection = "testPosts")
public class TestPosts {
    @Id
    private String id;
    @Field(value = "title")
    private String title;
    @DBRef(db = "testAuthors")
    private List<TestAuthors> authors;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TestAuthors> getAuthors() {
        return authors;
    }

    public void setAuthors(List<TestAuthors> authors) {
        this.authors = authors;
    }
}
