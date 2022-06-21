package net.edubovit.labyrinth.web.rest;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("csrf")
public class CsrfController {

    @GetMapping
    public CsrfToken csrfToken(CsrfToken token) {
        return token;
    }

}
