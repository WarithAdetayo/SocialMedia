package org.sm.backend.service;


import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sm.backend.dto.PostDTO;
import org.sm.backend.entity.Post;
import org.sm.backend.entity.QPost;
import org.sm.backend.entity.User;

import java.util.ArrayList;
import java.util.List;

public class PostService {
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final Logger logger;

    public PostService() {
        this("SocialMedia");
    }

    public PostService(String persistence_unit) {
        entityManagerFactory = Persistence.createEntityManagerFactory(persistence_unit);
        entityManager = entityManagerFactory.createEntityManager();
        logger = LogManager.getLogger(PostService.class);
    }

    public Post createPost(String title, User postedBy) {
        Post post = new Post();
        post.setTitle(title);
        post.setSmUser(postedBy);

        logger.info("New blog post created: " + post);

        return post;
    }

    public Post createPost(String title, String content, User postedBy) {
        Post post = createPost(title, postedBy);
        post.setContent(content);
        return post;
    }

    public void addPost(Post post) {
        entityManager.getTransaction().begin();

        entityManager.persist(post);

        entityManager.getTransaction().commit();

        logger.info("Post persisted into database: " + post);
    }

    public Post addPost(String title, User postedBy) {
        Post post = createPost(title, postedBy);
        addPost(post);
        return post;
    }

    public Post addPost(String title, String content, User postedBy) {
        Post post = createPost(title, postedBy);
        post.setContent(content);
        addPost(post);
        return post;
    }

    public Post findPost(long postId) {
        return entityManager.find(Post.class, postId);
    }

    public List<PostDTO> getAllPosts() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        QPost post = QPost.post;

        List<Post> posts = queryFactory.selectFrom(post).fetch();

        return toPostDTO(posts);
    }

    public PostDTO getPostById(long postId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QPost post = QPost.post;

        Post p = queryFactory
                .selectFrom(post)
                .where(post.postId.eq(postId))
                .fetchOne();
        if (p == null) {
            logger.warn(String.format("Post with id '%d' not found", postId));
            return null;
        }
        return toPostDTO(p);
    }

    public List<PostDTO> getAllPostsByUser(String username) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QPost post = QPost.post;

        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(post.smUser.username.eq(username))
                .fetch();

        return toPostDTO(posts);
    }

    public void deletePost(long postId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QPost post = QPost.post;

        queryFactory.delete(post)
                .where(post.postId.eq(postId))
                .execute();
    }

    public void deleteAllPostsByUser(String username) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QPost post = QPost.post;

        queryFactory.delete(post)
                .where(post.smUser.username.eq(username))
                .execute();

    }

    public static PostDTO toPostDTO(Post post) {
        return new PostDTO()
                .setContent(post.getContent())
                .setTitle(post.getTitle())
                .setDateCreated(post.getDateCreated())
                .setPostId(post.getPostId())
                .setSmUser(UserService.toUserDTO(post.getSmUser()));
    }

    public static List<PostDTO> toPostDTO(List<Post> posts) {
        List<PostDTO> postDTOS = new ArrayList<>();

        for (Post post : posts) {
            postDTOS.add(toPostDTO(post));
        }
        return postDTOS;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void close() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
