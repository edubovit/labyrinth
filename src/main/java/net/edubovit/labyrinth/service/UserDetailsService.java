package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.config.security.Roles;
import net.edubovit.labyrinth.exception.Exceptions;
import net.edubovit.labyrinth.repository.db.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(Exceptions.usernameNotFoundException(username));
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(emptyList())
                .roles(Roles.USER)
                .build();
    }

}
