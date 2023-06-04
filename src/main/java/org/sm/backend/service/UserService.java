package org.sm.backend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sm.backend.dto.UserDTO;
import org.sm.backend.entity.User;

public class UserService {
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final Logger logger;
    public UserService() {
        this("SocialMedia");
    }

    public UserService(String persistence_unit) {
        entityManagerFactory = Persistence.createEntityManagerFactory(persistence_unit);
        entityManager = entityManagerFactory.createEntityManager();
        logger = LogManager.getLogger(UserService.class);
    }

    public User createUser(String username, String password) {
        User user =  new User()
                .setUsername(username)
                .setPassword(password);
        logger.info("New User created: " + user);

        return user;
    }

    public void addUser(User user) {
        this.entityManager.getTransaction().begin();
        this.entityManager.persist(user);
        this.entityManager.getTransaction().commit();

        logger.info("User persisted in database: " + user);
    }

    public User addUser(String username, String password) {
        User existingUser = findUser(username);

        if (existingUser != null) {
            logger.error("User already exists");
            return null;
        }

        User user = this.createUser(username, password);
        this.addUser(user);
        return user;
    }

    public User findUser(String username) {
        return entityManager.find(User.class, username);
    }

    public static UserDTO toUserDTO(User user) {
        return new UserDTO()
                .setUsername(user.getUsername())
                .setDateJoined(user.getDateJoined());
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void close() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
