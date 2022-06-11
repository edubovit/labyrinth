package net.edubovit.labyrinth.service;

import net.edubovit.labyrinth.dto.UserCredentialsDTO;
import net.edubovit.labyrinth.dto.UserDTO;
import net.edubovit.labyrinth.entity.User;
import net.edubovit.labyrinth.exception.Exceptions;
import net.edubovit.labyrinth.exception.UsernameAlreadyExistsException;
import net.edubovit.labyrinth.repository.db.UserRepository;
import net.edubovit.labyrinth.util.SessionUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDTO getCurrent() {
        return new UserDTO(loadUserByAuth());
    }

    @Transactional
    public UserDTO signup(UserCredentialsDTO request) {
        log.info("signing up user {}, remote address: {}",
                request.username(), SessionUtils.getHttpServletRequest().getRemoteAddr());
        if (userRepository.existsByUsername(request.username())) {
            throw UsernameAlreadyExistsException.of(request.username());
        }
        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        log.info("successfully signed up user {} with ID {}", user.getUsername(), user.getId());
        return authenticate(request, user);
    }

    @Transactional(readOnly = true)
    public UserDTO authenticate(UserCredentialsDTO request) {
        log.info("authentication request from {}, remote address: {}",
                request.username(), SessionUtils.getHttpServletRequest().getRemoteAddr());
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(Exceptions.usernameNotFoundException(request.username()));
        return authenticate(request, user);
    }

    public void logout() {
        var session = SessionUtils.getHttpServletRequest().getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private UserDTO authenticate(UserCredentialsDTO request, User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        log.info("user {} has successfully authenticated", request.username());
        return new UserDTO(user);
    }

    private User loadUserByAuth() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("loading user from authentication: {}", authentication.toString());
        String username = SessionUtils.retrieveUsernameFromAuthentication(authentication);
        var user = userRepository.findByUsername(username)
                .orElseThrow(Exceptions.usernameNotFoundException(username));
        log.info("loaded user: {}", user.toString());
        return user;
    }

}
