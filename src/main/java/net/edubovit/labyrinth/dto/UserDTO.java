package net.edubovit.labyrinth.dto;

import net.edubovit.labyrinth.entity.User;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(UUID id, String username) {

    public UserDTO(User user) {
        this(user.getId(), user.getUsername());
    }

}
