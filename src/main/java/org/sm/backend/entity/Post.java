package org.sm.backend.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "SMPOST")
public class Post {
    @Id
    @Column(name = "postId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "dateCreated")
    private Date dateCreated;

    @JoinColumn(name = "smUser")
    @ManyToOne(targetEntity = User.class)
    private User smUser;

    public Post() {
        this.dateCreated = new Date();
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getSmUser() {
        return smUser;
    }

    public void setSmUser(User smUser) {
        this.smUser = smUser;
    }


    @Override
    public String toString() {
        return String.format("Post<title='%s', By='%s'>", title, smUser);
    }
}
