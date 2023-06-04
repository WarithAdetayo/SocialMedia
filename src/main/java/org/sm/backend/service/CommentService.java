package org.sm.backend.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sm.backend.dto.CommentDTO;
import org.sm.backend.entity.Comment;
import org.sm.backend.entity.Post;
import org.sm.backend.entity.QComment;
import org.sm.backend.entity.User;

import java.util.ArrayList;
import java.util.List;

public class CommentService {

    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final Logger logger;

    public CommentService() {
        this("SocialMedia");
    }

    public CommentService(String persistence_unit) {
        entityManagerFactory = Persistence.createEntityManagerFactory(persistence_unit);
        entityManager = entityManagerFactory.createEntityManager();
        logger = LogManager.getLogger(CommentService.class);
    }

    public Comment createComment(String content, Post post, User commentBy) {

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setSmPost(post);
        comment.setSmUser(commentBy);

        logger.info(String.format("New comment on %s created: %s", post, comment));

        return comment;
    }

    public void addComment(Comment comment) {
        entityManager.getTransaction().begin();
        entityManager.persist(comment);
        entityManager.getTransaction().commit();
        logger.info("Comment persisted into database: " + comment);
    }

    public Comment addComment(String content, Post post, User commentBy) {
        Comment comment = createComment(content, post, commentBy);
        addComment(comment);
        return comment;
    }

    public Comment findComment(long commentId) {
        return entityManager.find(Comment.class, commentId);
    }

    public static CommentDTO toCommentDTO(Comment comment) {
        return  new CommentDTO()
                .setCommentId(comment.getCommentId())
                .setContent(comment.getContent())
                .setSmPost(PostService.toPostDTO(comment.getSmPost()))
                .setSmUser(UserService.toUserDTO(comment.getSmUser()))
                .setDateCreated(comment.getDateCreated());
    }

    public static List<CommentDTO> toCommentDTO(List<Comment> comments) {
        List<CommentDTO> commentDTOs = new ArrayList<>();

        for (Comment comment : comments) {
            commentDTOs.add(toCommentDTO(comment));
        }
        return commentDTOs;
    }

    public CommentDTO getCommentById(long commentId) {
        QComment comment = QComment.comment;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        Comment c = queryFactory
                .selectFrom(comment)
                .where(comment.commentId.eq(commentId))
                .fetchOne();

        if (c == null) {
            logger.warn(String.format("Comment with id '%d' not found", commentId));
            return null;
        }

        return toCommentDTO(c);
    }

    public List<CommentDTO> getCommentsOnPost(long postId) {
        QComment comment = QComment.comment;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Comment> commentsOnPost = queryFactory
                .selectFrom(comment)
                .where(comment.smPost.postId.eq(postId))
                .orderBy(comment.dateCreated.asc())
                .fetch();
        return  toCommentDTO(commentsOnPost);
    }

    public List<CommentDTO> getCommentsByUser(String username) {
        QComment comment = QComment.comment;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Comment> commentsByUser = queryFactory
                .selectFrom(comment)
                .where(comment.smUser.username.eq(username))
                .orderBy(comment.smPost.dateCreated.asc())
                .orderBy(comment.dateCreated.asc())
                .fetch();
        return toCommentDTO(commentsByUser);
    }

    public List<CommentDTO> getAllComments() {
        QComment comment = QComment.comment;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Comment> allComments = queryFactory
                .selectFrom(comment)
                .orderBy(comment.smPost.dateCreated.asc())
                .orderBy(comment.dateCreated.asc())
                .fetch();

        return toCommentDTO(allComments);
    }

    public List<CommentDTO> getAllCommentsOnPostByUser(long postId, String username) {
        QComment comment = QComment.comment;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Comment> allComments = queryFactory
                .selectFrom(comment)
                .where(comment.smPost.postId.eq(postId))
                .where(comment.smUser.username.eq(username))
                .orderBy(comment.dateCreated.asc())
                .fetch();

        return toCommentDTO(allComments);
    }

    public void deleteComment(long commentId) {
        QComment comment = QComment.comment;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        queryFactory.delete(comment)
                .where(comment.commentId.eq(commentId))
                .execute();
    }

    public void deleteAllCommentsOnPost(long postId) {
        QComment comment = QComment.comment;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        queryFactory.delete(comment)
                .where(comment.smPost.postId.eq(postId))
                .execute();
    }

    public void deleteAllCommentsByUser(String username) {
        QComment comment = QComment.comment;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        queryFactory.delete(comment)
                .where(comment.smUser.username.eq(username))
                .execute();
    }

    public void deleteAllCommentsOnPostByUser(long postId, String username) {
        QComment comment = QComment.comment;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        queryFactory.delete(comment)
                .where(comment.smPost.postId.eq(postId))
                .where(comment.smUser.username.eq(username))
                .execute();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void close() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
