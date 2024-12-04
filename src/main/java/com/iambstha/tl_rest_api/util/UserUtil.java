package com.iambstha.tl_rest_api.util;

import com.iambstha.tl_rest_api.entity.User;
import com.iambstha.tl_rest_api.entity.enums.user.UserRole;
import com.iambstha.tl_rest_api.entity.enums.user.UserStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserUtil {

    public static Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getPrincipal() instanceof User userDetails) {
            return userDetails.getUserId();
        }
        return null;
    }

    public static boolean isUserLoggedIn() {
        return getUserId() != null;
    }

    public static String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = "";
        if (authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getPrincipal() instanceof User userDetails) {
            username = authentication.getName();
        }
        if (!Objects.equals(username, "")) {
            return username;
        }
        return null;
    }

    public static Collection<? extends GrantedAuthority> getRoles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public static UserStatus getUserStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getPrincipal() instanceof User userDetails) {
            return userDetails.getStatus();
        }
        return null;
    }

    public static boolean isAdmin() {
        Collection<? extends GrantedAuthority> roles = getRoles();
        return roles != null && roles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet())
                .contains("ROLE_" + UserRole.ADMIN);
    }

    public static boolean isUser() {
        Collection<? extends GrantedAuthority> roles = getRoles();
        return roles != null && roles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet())
                .contains("ROLE_" + UserRole.USER);
    }

}
