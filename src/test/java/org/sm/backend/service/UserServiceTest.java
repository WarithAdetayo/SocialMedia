package org.sm.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sm.backend.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    static UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService("SocialMediaTest");
    }

    @Test
    void createUserAndCheckPassword() {
        User user = userService.createUser("Test User", "user password");

        assertNotNull(user);
        assertTrue(user.checkPassword("user password"));
    }

    @Test
    void addUser() {
        User user = userService.addUser("Test User2", "User 2 password");
        User u = userService.getEntityManager().find(User.class, user.getUsername());

        assertNotNull(u);
    }
}