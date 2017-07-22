package com.lancefallon.config.security;

import com.lancefallon.services.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * "custom" auth provider. really all this does is use the user details service, and does not check for a pwd.
 * uses a custom shouldAuthenticateAgainstThirdPartySystem method to determine if auth check should even be attempted
 *
 * @author lfallon
 */
@Component
public class CustomAuthenticationProvider
        implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordService passwordService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        // check credentials
        UserDetails user = userDetailsService.loadUserByUsername(name);
        if (this.passwordService.matchPassword(password, user.getPassword())) {
            return new CustomUsernamePasswordAuthenticationToken(
                    user, password, user.getAuthorities());
        }

        return null;
    }

    private boolean shouldAuthenticateAgainstThirdPartySystem() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }
}
