package com.donat.donchess.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication)
		throws AuthenticationException {

		String name = authentication.getName();
		String password = null;
		if (authentication.getCredentials() != null) {
			password = authentication.getCredentials().toString();
		}

		return new UsernamePasswordAuthenticationToken(name, password);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}