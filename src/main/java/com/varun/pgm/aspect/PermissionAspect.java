package com.varun.pgm.aspect;

import com.varun.pgm.annotation.RequirePermission;
import com.varun.pgm.exception.AccessDeniedException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    @Before("@annotation(requirePermission)")
    public void checkPermission(RequirePermission requirePermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        String required = requirePermission.value();

        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(authority -> {
                    String auth = authority.getAuthority();
                    return auth.equalsIgnoreCase(required)
                            || auth.startsWith("ADMIN_")
                            || auth.equalsIgnoreCase("SUPER_ADMIN")
                            || auth.equalsIgnoreCase("ROLE_ADMIN")
                            || auth.equalsIgnoreCase("ROLE_SUPER_ADMIN");
                });

        if (!hasPermission) {
            throw new AccessDeniedException("Access denied: " + required + " permission required");
        }
    }
}
