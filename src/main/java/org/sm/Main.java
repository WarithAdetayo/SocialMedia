package org.sm;

import org.sm.backend.BackendController;
import org.sm.backend.dto.CommentDTO;
import org.sm.backend.dto.PostDTO;
import org.sm.util.ResponseMessage;

import java.util.List;
import java.util.Scanner;

interface Screen {
    Screen display();
}


public class Main {

    private static BackendController backend;
    public static void main(String[] args) {

        backend = BackendController.getInstance();

        Screen currentScreen = Main::landingScreen;

        while (currentScreen != null) {
            currentScreen = currentScreen.display();
        }

    }

    public static Screen exitProgram() {
        System.out.println("Exiting program");
        backend.shutDown();
        return null;
    }

    public static Screen landingScreen() {
        System.out.println("1. Sign Up");
        System.out.println("2. Sign In");
        System.out.println("3. Exit Program");

        int choice = acceptUserChoice(1, 3);

        switch (choice) {
            case 1 -> {
                return Main::signUpScreen;
            }
            case 2 -> {
                return Main::signInScreen;
            }
            case 3 -> {
                return Main::exitProgram;
            }
        }
        return Main::landingScreen;
    }

    public static Screen signUpScreen() {
        System.out.println("<<<<<<<<<< Sign in to you account >>>>>>>>>>\n");

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Your UserName: ");
        String username = sc.nextLine();

        String password;

        while (true) {
            System.out.print("Enter a password: ");
            String password1 = sc.nextLine();
            System.out.print("Confirm Password: ");
            String password2 = sc.nextLine();

            if (!password1.equals(password2)) {
                System.out.println("Password Mismatch!!!");
                continue;
            }

            password = password1;
            break;
        }

        ResponseMessage<Boolean> message = backend.signUpUser(username, password);

        if (!message.getResponseData()) {
            System.out.println("Sorry, Unable to Sign up: " + message.getMessage());
        } else {
            System.out.println("User Registration Successfully");
        }

        waitOnUser(null);

        return Main::landingScreen;

    }

    public static Screen signInScreen() {
        System.out.println("<<<<<<<<<< Sign in to your account >>>>>>>>>>\n");

        int tries = 3;
        Scanner sc = new Scanner(System.in);

        while (tries > 0) {
            System.out.print("Enter Your UserName: ");
            String username = sc.nextLine();
            System.out.print("Enter a password: ");
            String password = sc.nextLine();

            ResponseMessage<Boolean> response = backend.signInUser(username, password);

            if (response.getResponseData()) {
                System.out.println("Sign in successful");
                return Main::dashBoard;
            }
            System.out.println(response.getMessage());
            tries--;
        }
        return Main::landingScreen;
    }

    public static Screen dashBoard() {
        System.out.println("<<<<<<<<<< Dashboard >>>>>>>>>>");
        System.out.println("1. View Posts");
        System.out.println("2. Create Posts");
        System.out.println("3. Sign Out");

        int choice = acceptUserChoice(1, 3);

        switch (choice) {
            case 1 -> {
                return Main::viewPosts;
            }
            case 2 -> {
                return Main::createPosts;
            }
            case 3 -> {
                backend.signOutUser();
                return landingScreen();
            }
        }

        return  Main::dashBoard;
    }

    public static Screen viewPosts() {
        System.out.println("<<<<<<<<<< Posts >>>>>>>>>>");

        ResponseMessage<List<PostDTO>> response = backend.getAllPosts();
        List<PostDTO> allPosts = response.getResponseData();
        if ( allPosts == null || allPosts.size() == 0) {
            System.out.println("No Post found. Return to dashboard to create new post!!!");
            waitOnUser(null);
            return Main::dashBoard;
        }

        while (true) {
            System.out.println("Select a Post to View");

            for (int i = 0; i < allPosts.size(); i++) {
                PostDTO p = allPosts.get(i);
                System.out.printf(
                        "%d. Title: %s By %s\n\t\tDate: %s\n",
                        i + 1, p.getTitle(), p.getSmUser().getUsername(), p.getDateCreated()
                );
            }

            System.out.printf("%d. Return To dashboard\n\n", allPosts.size() + 1);

            int userChoice = acceptUserChoice(1, allPosts.size() + 1);

            if (userChoice == allPosts.size() + 1)
                break;

            viewAPost(allPosts.get(userChoice - 1));
        }

        return Main::dashBoard;
    }

    public static void viewAPost(PostDTO post) {
        System.out.printf("\n\n%s by %s\n", post.getTitle(), post.getSmUser().getUsername());
        System.out.printf("Created: %s\n", post.getDateCreated());

        System.out.printf("\n%s\n\n", post.getContent());

        ResponseMessage<List<CommentDTO>> response = backend.getCommentOnPost(post.getPostId());
        List<CommentDTO> comments = response.getResponseData();

        if (comments != null && comments.size() > 0) {
            System.out.println("\n\nComments:");

            for (CommentDTO comment : comments) {
                System.out.printf(
                        "\t%s: %s\n\t\t\t(%s)\n",
                        comment.getSmUser().getUsername(),
                        comment.getContent(), comment.getDateCreated()
                );
            }
        }

        System.out.println("1. Make a comment");
        System.out.println("2. Return back");

        int choice = acceptUserChoice(1, 2);

        if (choice == 1) {
            Scanner sc = new Scanner(System.in);
            System.out.print("What is your comment?: ");
            String comment = sc.nextLine();

            ResponseMessage<Boolean> res = backend.makeCommentOnPost(post.getPostId(), comment);

            if (res.getResponseData()) {
                System.out.println("Comment added to post");
            } else {
                System.out.println(response.getMessage());
            }

            viewAPost(post);
        }

    }

    public static Screen createPosts() {
        Scanner sc = new Scanner(System.in);
        System.out.println("<<<<<<<<<< Create Post >>>>>>>>>>");

        System.out.print("\nEnter the Title of the Post: ");
        String title = sc.nextLine();

        System.out.println("Carefully type in the content of the post");
        System.out.print(": ");
        String content = sc.nextLine();

        ResponseMessage<Boolean> res = backend.createPost(title, content);
        if (res.getResponseData()) {
            System.out.println("Post Created Successful");
        } else {
            System.out.println(res.getMessage());
        }
        waitOnUser("Press Enter to return to Dashboard");
        return Main::dashBoard;
    }

    public static void waitOnUser(String msg) {
        Scanner sc = new Scanner(System.in);
        if (msg == null)
            msg = "Press enter to continue";
        System.out.print(msg);
        sc.nextLine();
    }

    public static int acceptUserChoice(int minChoice, int maxChoice) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print(": ");

            try {
                int choice = sc.nextInt();

                if (choice >= minChoice && choice <= maxChoice ) {
                    return choice;
                } else {
                    System.out.format("Choice number between (%d - %d)\n", minChoice, maxChoice);
                }
            } catch (Exception e) {
                System.out.format("Pls enter a number from list (%d - %d)\n", minChoice, maxChoice);
                sc.nextLine();
            }
        }
    }
}