package org.sm.backend.dto;

import java.util.Date;

public class CommentDTO {

    private long commentId;
    private String content;
    private PostDTO smPost;
    private UserDTO smUser;
    private Date dateCreated;

    public long getCommentId() {
        return commentId;
    }

    public CommentDTO setCommentId(long commentId) {
        this.commentId = commentId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public CommentDTO setContent(String content) {
        this.content = content;
        return this;
    }

    public PostDTO getSmPost() {
        return smPost;
    }

    public CommentDTO setSmPost(PostDTO post) {
        this.smPost = post;
        return this;
    }

    public UserDTO getSmUser() {
        return smUser;
    }

    public CommentDTO setSmUser(UserDTO smUser) {
        this.smUser = smUser;
        return this;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public CommentDTO setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Comment<Post='%s', By='%s', DateCreated='%s'>", smPost, smUser, dateCreated);
    }
}
