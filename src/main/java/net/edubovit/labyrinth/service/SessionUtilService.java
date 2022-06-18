package net.edubovit.labyrinth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
@Slf4j
public class SessionUtilService {

    public Optional<HttpSession> getSession() {
        return Optional.ofNullable(getHttpServletRequest().getSession(false));
    }

    public void putToSession(String key, Object value) {
        getHttpServletRequest().getSession(true).setAttribute(key, value);
    }

    public String getRemoteAddress() {
        return getHttpServletRequest().getRemoteAddr();
    }

    public String getXRealIP() {
        return getHttpServletRequest().getHeader("X-Real-IP");
    }

    public String getUsername() {
        return retrieveUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    }

    private String retrieveUsernameFromAuthentication(Authentication authentication) {
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

    private HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

}
