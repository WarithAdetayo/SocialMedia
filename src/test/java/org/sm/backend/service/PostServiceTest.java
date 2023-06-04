package org.sm.backend.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sm.backend.dto.PostDTO;
import org.sm.backend.entity.Post;
import org.sm.backend.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {

    static PostService postService;
    static UserService userService;

    @BeforeAll
    static void setUp() {
        postService = new PostService("SocialMediaTest");
        userService = new UserService("SocialMediaTest");

    }

    @AfterEach
    void clearDatabase() {
        postService.getEntityManager().getTransaction().begin();
        postService.getEntityManager().createQuery("DELETE FROM Post").executeUpdate();
        postService.getEntityManager().getTransaction().commit();

        userService.getEntityManager().getTransaction().begin();
        userService.getEntityManager().createQuery("DELETE FROM User").executeUpdate();
        userService.getEntityManager().getTransaction().commit();
    }

    @AfterAll
    static void tearDown() {
        postService.close();
        userService.close();
    }

    @Test
    void createPost() {
        User user = userService.createUser("TestUserName", "Password");
        Post post = postService.createPost("Some Post Title", user);

        assertNotNull(post);
    }

    @Test
    void addPost() {

        User user = userService.addUser("UserName", "Password2");
        Post post = postService.addPost("Title", user);

        Post p = postService.findPost(post.getPostId());
        assertNotNull(p);

    }

    @Test
    void getAllPosts() {

        User user1 = userService.addUser("UserName", "Password2");
        Post user1post = postService.addPost("Title", user1);

        User user2 = userService.addUser("AnotherUSer", "Password3");
        Post user2Post = postService.addPost("PostTitle", user2);

        List<PostDTO> allPosts = postService.getAllPosts();

        assertEquals(2, allPosts.size());
    }

    @Test
    void getPostById() {
        User user = userService.addUser("NewUser", "upassword");
        Post userpost = postService.addPost("TitlePost", user);

        PostDTO fetchedPost = postService.getPostById(userpost.getPostId());
        assertNotNull(fetchedPost);
        assertEquals(userpost.getTitle(), fetchedPost.getTitle());
    }

    @Test
    void getAllPostsByUser() {
        User user1 = userService.addUser("User1", "Password2");
        Post user1post1 = postService.addPost("Title1", user1);
        Post user1post2 = postService.addPost("Title2", user1);

        User user2 = userService.addUser("User2", "Password3");
        Post user2Post = postService.addPost("PostTitle1", user2);

        List<PostDTO> postsByUser1 = postService.getAllPostsByUser(user1.getUsername());
        List<PostDTO> postsByUser2 = postService.getAllPostsByUser(user2.getUsername());
        assertEquals(2, postsByUser1.size());
        assertEquals(1, postsByUser2.size());
    }
}