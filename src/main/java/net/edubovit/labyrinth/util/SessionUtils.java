package net.edubovit.labyrinth.util;

import net.edubovit.labyrinth.config.security.Constants;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@UtilityClass
@Slf4j
public final class SessionUtils {

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getHttpServletResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }

    public static String getUsername() {
        var authentication = (Authentication) getHttpServletRequest()
                .getSession(false)
                .getAttribute(Constants.SESSION_AUTHENTICATION_ATTRIBUTE);
        return retrieveUsernameFromAuthentication(authentication);
    }

    public static String retrieveUsernameFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        return switch (principal) {
            case UserDetails userDetails -> userDetails.getUsername();
            default -> {
                log.warn("unknown Principal type {} in {}, using .toString() as fallback",
                        principal.getClass(), authentication.getClass());
                yield principal.toString();
            }
        };
    }

}
