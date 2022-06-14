package net.edubovit.labyrinth.exception;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.function.Supplier;

@UtilityClass
public final class Exceptions {

    public static Supplier<UsernameNotFoundException> usernameNotFoundException(String username) {
        return () -> new UsernameNotFoundException("user %s not found".formatted(username));
    }

}
