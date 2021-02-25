package com.kenstevens.stratinit.wicket.framework;

import com.kenstevens.stratinit.config.StratinitUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

public final class AuthHelper {
    private AuthHelper() {
    }

    static String getUsername() {
        if (isAnonymous()) {
            return null;
        }
        StratinitUserDetails user = (StratinitUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();
    }

    static List<String> getRoles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public static boolean isSignedIn() {
        return !isAnonymous();
    }

    private static boolean isAnonymous() {
        return SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken;
    }
}
