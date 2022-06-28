package net.edubovit.labyrinth.web.rest;

import net.edubovit.labyrinth.config.security.PreAuthorizeUser;
import net.edubovit.labyrinth.service.CommandService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("command")
@RequiredArgsConstructor
public class CommandController {

    private final CommandService service;

    @PostMapping({ "chaos/{delayMillis}", "chaos" })
    @PreAuthorizeUser
    public void chaos(@PathVariable(required = false) Integer delayMillis) {
        service.chaos(delayMillis);
    }

    @PostMapping({ "solve/{delayMillis}", "solve" })
    @PreAuthorizeUser
    public void solve(@PathVariable(required = false) Integer delayMillis) {
        service.solve(delayMillis);
    }

    @PostMapping("stop")
    @PreAuthorizeUser
    public void stop() {
        service.stop();
    }

}
