package com.example.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class CustomMockUserSecurityContextFactory implements WithSecurityContextFactory<CustomMockUser> {
    @Override
    public SecurityContext createSecurityContext(CustomMockUser annotation) {
        String email = annotation.email();

        Authentication auth = new UsernamePasswordAuthenticationToken(email, "",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);

        return context;
    }
}
