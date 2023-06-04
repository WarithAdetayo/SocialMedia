package org.sm.backend.dto;

import org.sm.backend.entity.User;

import java.util.Date;

public class PostDTO {
    private long postId;
    private String title;
    private String content;
    private UserDTO smUser;
    private Date dateCreated;

    public PostDTO() {}

    public long getPostId() {
        return postId;
    }

    public PostDTO setPostId(long postId) {
        this.postId = postId;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Post<title='%s', By='%s', DateCreated='%s'>", title, smUser, dateCreated);
    }

    public String getTitle() {
        return title;
    }

    public PostDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public PostDTO setContent(String content) {
        this.content = content;
        return  this;
    }

    public UserDTO getSmUser() {
        return smUser;
    }

    public PostDTO setSmUser(UserDTO smUser) {
        this.smUser = smUser;
        return this;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public PostDTO setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }
}
