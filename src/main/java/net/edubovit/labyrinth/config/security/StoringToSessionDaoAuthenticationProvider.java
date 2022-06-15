package net.edubovit.labyrinth.config.security;

import net.edubovit.labyrinth.service.SessionUtilService;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpSession;

@Configuration
public class StoringToSessionDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private final SessionUtilService sessionUtilService;

    public StoringToSessionDaoAuthenticationProvider(UserDetailsService userDetailsService,
                                                     PasswordEncoder passwordEncoder,
                                                     SessionUtilService sessionUtilService) {
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
        this.sessionUtilService = sessionUtilService;
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        var successAuthentication = super.createSuccessAuthentication(principal, authentication, user);
        sessionUtilService.getSession().ifPresent(HttpSession::invalidate);
        sessionUtilService.putToSession(Constants.SESSION_AUTHENTICATION_ATTRIBUTE, successAuthentication);
        return successAuthentication;
    }

}
