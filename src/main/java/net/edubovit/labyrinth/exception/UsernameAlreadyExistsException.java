package net.edubovit.labyrinth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameAlreadyExistsException extends RuntimeException {

    private UsernameAlreadyExistsException(String message) {
        super(message);
    }

    public static UsernameAlreadyExistsException of(String username) {
        return new UsernameAlreadyExistsException("username %s already exists".formatted(username));
    }

}
