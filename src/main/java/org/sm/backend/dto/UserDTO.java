package org.sm.backend.dto;

import java.util.Date;

public class UserDTO {
    private String username;

    private Date dateJoined;

    public UserDTO() {}

    public String getUsername() {
        return username;
    }

    public UserDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public Date getDateJoined() {
        return dateJoined;
    }

    public UserDTO setDateJoined(Date dateJoined) {
        this.dateJoined = dateJoined;
        return this;
    }

    @Override
    public String toString() {
        return String.format("User<name='%s', DateJoined='%s'>", username, dateJoined);
    }
}
