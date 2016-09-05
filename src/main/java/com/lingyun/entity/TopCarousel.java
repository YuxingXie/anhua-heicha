package com.lingyun.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by Administrator on 2015/12/31.
 */
@Document(collection = "topCarousel")
public class TopCarousel {
    @Id
    private String id;
    private List<String[]> adContent;
    private Integer priority;
    private String name;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String[]> getAdContent() {
        return adContent;
    }

    public void setAdContent(List<String[]> adContent) {
        this.adContent = adContent;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
