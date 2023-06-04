package org.sm.backend.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "SMCOMMENT")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentId")
    private long commentId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "smPost")
    private Post smPost;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "smUser")
    private User smUser;

    @Column(name = "dateCreated")
    private Date dateCreated;

    public Comment() {
        this.dateCreated = new Date();
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Post getSmPost() {
        return smPost;
    }

    public void setSmPost(Post post) {
        this.smPost = post;
    }

    public User getSmUser() {
        return smUser;
    }

    public void setSmUser(User smUser) {
        this.smUser = smUser;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return String.format(
                "Comment<Post Title='%s' Commenter='%s'>",
                this.smPost.getTitle(), this.smUser.getUsername()
        );
    }
}
