package org.sm.backend;

import org.sm.backend.dto.CommentDTO;
import org.sm.backend.dto.PostDTO;
import org.sm.backend.entity.Comment;
import org.sm.backend.entity.Post;
import org.sm.backend.entity.User;
import org.sm.backend.service.CommentService;
import org.sm.backend.service.PostService;
import org.sm.backend.service.UserService;
import org.sm.util.ResponseMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;


//@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.METHOD)
//@interface LoginRequired {}


public class BackendController {
    private static BackendController instance = null;

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private User signedInUSer;

    private BackendController() {
        userService = new UserService("SocialMedia");
        postService = new PostService("SocialMedia");
        commentService = new CommentService("SocialMedia");
        signedInUSer = null;
    }

    public ResponseMessage<Boolean> signUpUser(String username, String password) {
        User user = userService.findUser(username);

        if (user != null)
            return  new ResponseMessage<>(400,"User exists already", false);

        user = userService.addUser(username, password);

        if (user != null) {
            return  new ResponseMessage<>(201, "User Created Successfully", true);
        }
        return new ResponseMessage<>(505,"Internal Server Error", false);

    }

    public ResponseMessage<Boolean> signInUser(String username, String password) {
        User user = userService.findUser(username);

        if (user != null) {
            if (user.checkPassword(password)) {
                this.signedInUSer = user;
                return  new ResponseMessage<>(200, "Sign in successfully", true);
            }
        }
        return new ResponseMessage<>(400, "Incorrect Username or Password", false);
    }

    public ResponseMessage<Boolean> signOutUser() {
        if (this.signedInUSer != null) {
            this.signedInUSer = null;
            return new ResponseMessage<>(200, "User Signed out successfully", true);
        }
        return new ResponseMessage<>(400, "User not signed in", false);
    }

    public ResponseMessage<Boolean> createPost(String title, String content) {
        if (this.signedInUSer == null) {
            return new ResponseMessage<>(401, "User not signed in", false);
        }

        Post post = postService.addPost(title, content, this.signedInUSer);
        if (post != null) {
            return new ResponseMessage<>(201, "Blog Post Created", true);
        }

        return  new ResponseMessage<>(505, "Internal Server Error", false);
    }

    public ResponseMessage<List<PostDTO>> getAllPosts() {
        if (this.signedInUSer == null) {
            return new ResponseMessage<>(401, "User not signed in", null);
        }

        List<PostDTO> allPosts = postService.getAllPosts();

        return new ResponseMessage<>(200, "Request OK", allPosts);
    }

    public ResponseMessage<List<PostDTO>> getAllPostsByUser() {
        if (this.signedInUSer == null) {
            return new ResponseMessage<>(401, "User not signed in", null);
        }

        List<PostDTO> allPosts = postService.getAllPostsByUser(this.signedInUSer.getUsername());

        return new ResponseMessage<>(200, "Request OK", allPosts);

    }

    public ResponseMessage<Boolean> makeCommentOnPost(long postId, String comment) {
        if (this.signedInUSer == null) {
            return new ResponseMessage<>(401, "User not signed in", false);
        }

        Post post = postService.findPost(postId);

        if (post == null)
            return new ResponseMessage<>(400, "Post with ID not found", false);

        Comment userComment = commentService.addComment(comment, post, this.signedInUSer);

        if (userComment != null) {
            return new ResponseMessage<>(201, "Comment added to post", true);
        }
        return new ResponseMessage<>(505, "Internal Server Error", false);
    }

    public ResponseMessage<List<CommentDTO>> getCommentOnPost(long postId) {
        if (this.signedInUSer == null) {
            return new ResponseMessage<>(401, "User not signed in", null);
        }

        List<CommentDTO> comments = commentService.getCommentsOnPost(postId);

        return new ResponseMessage<>(200, "Request OK", comments);
    }

    public ResponseMessage<List<CommentDTO>> getCommentOnPostByUser(long postId) {
        if (this.signedInUSer == null) {
            return new ResponseMessage<>(401, "User not signed in", null);
        }

        List<CommentDTO> comments = commentService.getAllCommentsOnPostByUser(postId, this.signedInUSer.getUsername());

        return new ResponseMessage<>(200, "Request OK", comments);
    }

    public ResponseMessage<Boolean> deleteComment(long commentId) {
        if (this.signedInUSer == null) {
            return new ResponseMessage<>(401, "User not signed in", null);
        }

        Comment comment = commentService.findComment(commentId);

        if (comment == null)
            return new ResponseMessage<>(400, "Comment Not Found", false);

        if (!comment.getSmUser().getUsername().equals(this.signedInUSer.getUsername()) || // Not the Owner of the comment
                !comment.getSmPost().getSmUser().getUsername().equals(this.signedInUSer.getUsername()) // Not the owner of the post
        ) {
            return new ResponseMessage<>(401, "You cannot delete another user's comment", false);
        }

        commentService.deleteComment(commentId);

        return new ResponseMessage<>(200, "Comment Delete", true);
    }

    public ResponseMessage<Boolean> deletePost(long postId) {
        if (this.signedInUSer == null) {
            return new ResponseMessage<>(401, "User not signed in", null);
        }

        Post post = postService.findPost(postId);

        if (post == null)
            return new ResponseMessage<>(400, "Post Not Found", false);

        if (!post.getSmUser().getUsername().equals(this.signedInUSer.getUsername())) {
            return new ResponseMessage<>(401, "You cannot delete another user's post", false);
        }

        commentService.deleteAllCommentsOnPost(postId); // Deletes all comments on the post
        postService.deletePost(postId);
        return new ResponseMessage<>(200, "Post Deleted", true);
    }

    public  ResponseMessage<Boolean> deleteAllMyPosts() {

        if (this.signedInUSer == null) {
            return new ResponseMessage<>(401, "User not signed in", null);
        }

        postService.deleteAllPostsByUser(this.signedInUSer.getUsername());
        return new ResponseMessage<>(200, "Posts Deleted", true);
    }

    public static BackendController getInstance() {
        if (instance == null) {
            instance = new BackendController();
        }
        return  instance;
    }

    public void shutDown() {
        userService.close();
        postService.close();
        commentService.close();
    }
}
