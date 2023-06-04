package org.sm.backend.service;

import org.junit.jupiter.api.*;
import org.sm.backend.dto.CommentDTO;
import org.sm.backend.entity.Comment;
import org.sm.backend.entity.Post;
import org.sm.backend.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {

    static CommentService commentService;
    static PostService postService;
    static UserService userService;

    @BeforeAll
    static void setUp() {
        commentService = new CommentService("SocialMediaTest");
        postService = new PostService("SocialMediaTest");
        userService = new UserService("SocialMediaTest");
    }

    @AfterAll
    static void tearDown() {
        commentService.close();
        postService.close();
        userService.close();
    }

    @AfterEach
    void cleanDatabase() {
        commentService.getEntityManager().getTransaction().begin();
        commentService.getEntityManager().createQuery("DELETE FROM Comment").executeUpdate();
        commentService.getEntityManager().getTransaction().commit();

        postService.getEntityManager().getTransaction().begin();
        postService.getEntityManager().createQuery("DELETE FROM Post").executeUpdate();
        postService.getEntityManager().getTransaction().commit();

        userService.getEntityManager().getTransaction().begin();
        userService.getEntityManager().createQuery("DELETE FROM User").executeUpdate();
        userService.getEntityManager().getTransaction().commit();
    }

    @Test
    void createComment() {
        User poster = userService.createUser("PosterUsername", "PosterPassword");
        Post post = postService.createPost("Blog Post Title", poster);
        User commenter = userService.createUser("CommenterUsername", "CommenterPassword");
        Comment comment = commentService.createComment("Some Comment", post, commenter);

        assertNotNull(comment);
        assertEquals(post.getPostId(), comment.getSmPost().getPostId());
        assertEquals(commenter.getUsername(), comment.getSmUser().getUsername());
    }

    @Test
    void addComment() {
        User poster = userService.addUser("PosterUsername", "PosterPassword");
        Post post = postService.addPost("Blog Post Title", poster);
        User commenter = userService.addUser("CommenterUsername", "CommenterPassword");
        Comment comment = commentService.addComment("Some Comment", post, commenter);

        Comment c = commentService.findComment(comment.getCommentId());
        assertNotNull(c);
    }

    @Test
    void getCommentById() {
        User poster = userService.addUser("PosterUsername", "PosterPassword");
        Post post = postService.addPost("Blog Post Title", poster);
        User commenter = userService.addUser("CommenterUsername", "CommenterPassword");
        Comment comment = commentService.addComment("Some Comment", post, commenter);

        CommentDTO c = commentService.getCommentById(comment.getCommentId());
        assertNotNull(c);
    }

    @Test
    void getCommentsOnPost() {
        User user1 = userService.addUser("User1", "user1password");
        User user2 = userService.addUser("User2", "user2password");
        User user3 = userService.addUser("User3", "user3password");

        Post post = postService.addPost("Blog Post Title by User 1", user1);

        Comment comment1 = commentService.addComment("Some Comment by User 2", post, user2);
        Comment comment2 = commentService.addComment("Some Comment by User 3", post, user3);

        List<CommentDTO> commentsOnPost = commentService.getCommentsOnPost(post.getPostId());

        assertEquals(2, commentsOnPost.size());
    }

    @Test
    void getCommentsByUser() {
        User user1 = userService.addUser("User1", "user1password");
        User user2 = userService.addUser("User2", "user2password");
        User user3 = userService.addUser("User3", "user3password");

        Post post1 = postService.addPost("Blog Post Title by User 1", user1);
        Post post2 = postService.addPost("Blog Post Title by User 2", user2);

        Comment comment1 = commentService.addComment("Some Comment by User 2", post1, user2);
        Comment comment2 = commentService.addComment("Some Comment by User 3", post2, user3);
        Comment comment3 = commentService.addComment("Some More Comment by User 3", post2, user3);

        List<CommentDTO> allCommentsByUser2 = commentService.getCommentsByUser(user2.getUsername());
        List<CommentDTO> allCommentsByUser3 = commentService.getCommentsByUser(user3.getUsername());

        assertEquals(1, allCommentsByUser2.size());
        assertEquals(2, allCommentsByUser3.size());

        allCommentsByUser3.forEach(System.out::println);
        allCommentsByUser2.forEach(System.out::println);
    }

    @Test
    void getAllComments() {
        User user1 = userService.addUser("User1", "user1password");
        User user2 = userService.addUser("User2", "user2password");
        User user3 = userService.addUser("User3", "user3password");

        Post post1 = postService.addPost("Blog Post Title by User 1", user1);
        Post post2 = postService.addPost("Blog Post Title by User 2", user2);

        Comment comment2 = commentService.addComment("Some Comment by User 3", post2, user3);
        Comment comment1 = commentService.addComment("Some Comment by User 2", post1, user2);
        Comment comment3 = commentService.addComment("Some More Comment by User 3", post2, user3);

        List<CommentDTO> allComments = commentService.getAllComments();

        assertEquals(3, allComments.size());
        allComments.forEach(System.out::println);
    }
}