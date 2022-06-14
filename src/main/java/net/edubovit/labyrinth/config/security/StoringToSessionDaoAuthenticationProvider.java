package net.edubovit.labyrinth.config.security;

import net.edubovit.labyrinth.util.SessionUtils;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class StoringToSessionDaoAuthenticationProvider extends DaoAuthenticationProvider {

    public StoringToSessionDaoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        var successAuthentication = super.createSuccessAuthentication(principal, authentication, user);
        var servletRequest = SessionUtils.getHttpServletRequest();
        var oldSession = servletRequest.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        servletRequest.getSession().setAttribute(Constants.SESSION_AUTHENTICATION_ATTRIBUTE, successAuthentication);
        return successAuthentication;
    }

}
