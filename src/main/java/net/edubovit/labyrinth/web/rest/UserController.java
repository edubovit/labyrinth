package net.edubovit.labyrinth.web.rest;

import net.edubovit.labyrinth.config.security.PreAuthorizeAnonymous;
import net.edubovit.labyrinth.config.security.PreAuthorizeUser;
import net.edubovit.labyrinth.dto.UserCredentialsDTO;
import net.edubovit.labyrinth.dto.UserDTO;
import net.edubovit.labyrinth.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("me")
    @PreAuthorizeUser
    public UserDTO getCurrent() {
        return service.getCurrent();
    }

    @PostMapping("signup")
    @PreAuthorizeAnonymous
    public UserDTO signup(@Valid @RequestBody UserCredentialsDTO request) {
        return service.signup(request);
    }

    @PostMapping("login")
    @PreAuthorizeAnonymous
    public UserDTO authenticate(@Valid @RequestBody UserCredentialsDTO request) {
        return service.authenticate(request);
    }

    @PostMapping("logout")
    @PreAuthorizeUser
    public void logout() {
        service.logout();
    }

}
