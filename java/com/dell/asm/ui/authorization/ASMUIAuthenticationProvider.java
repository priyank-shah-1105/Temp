package com.dell.asm.ui.authorization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.dell.asm.asmcore.user.model.AuthenticateResponse;
import com.dell.asm.libext.tomcat.AsmManagerInitLifecycleListener;
import com.dell.asm.ui.adapter.service.UserServiceAdapter;
import com.dell.asm.ui.spring.ASMUserDetails;

/**
 * The Class VISPortalAuthenticationProvider.
 */
@Component("asmuiAuthenticationProvider")
public class ASMUIAuthenticationProvider implements AuthenticationProvider {
    private static final Logger log = Logger.getLogger(ASMUIAuthenticationProvider.class);
    private static String STATUS;
    private final UserServiceAdapter userServiceAdapter;

    @Autowired
    public ASMUIAuthenticationProvider(UserServiceAdapter userServiceAdapter) {
        this.userServiceAdapter = userServiceAdapter;
    }

    public static void setSTATUS(String STATUS) {
        ASMUIAuthenticationProvider.STATUS = STATUS;
    }

    /**
     *
     * @param auth
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        UserDetails user = authenticateUser((UsernamePasswordAuthenticationToken) auth);

        // Override eraseCredentials method on UsernamePasswordAuthenticationToken
        // to do nothing in order to save password for later retrieval
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                user,
                auth.getCredentials(),
                user.getAuthorities())
        {
            public void eraseCredentials() { /* DO NOT ERASE PASSWORD */ }
        };

        result.setDetails(auth.getDetails());

        return result;
    }

    /**
     *
     * @param auth
     * @return
     */
    @Override
    public boolean supports(Class<?> auth) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(auth));
    }

    /**
     * Authenticate user.
     *
     * @param auth the auth
     * @return the user details
     * @throws AuthenticationException the authentication exception
     */
    private UserDetails authenticateUser(
            UsernamePasswordAuthenticationToken auth) throws AuthenticationException {

        if (STATUS == null || !STATUS.equals(AsmManagerInitLifecycleListener.READY)) {
            STATUS = AsmManagerInitLifecycleListener.getStatus();
            if (STATUS == null || !STATUS.equals(AsmManagerInitLifecycleListener.READY)) {
                throw new BadCredentialsException("error.initialization");
            }
        }

        if (StringUtils.isBlank((String)auth.getPrincipal()) || StringUtils.isBlank((String)auth.getCredentials())) {
            log.error("Authentication token was invalid");
            throw new BadCredentialsException(null);
        }

        String username = (String) auth.getPrincipal();
        String password = (String) auth.getCredentials();

        final AuthenticateResponse token = userServiceAdapter.login(username, password, null);
        if (StringUtils.isEmpty(token.getRole())) {
            throw new AuthenticationCredentialsNotFoundException(null);
        }

        List<GrantedAuthority> lstAuthorities = new ArrayList<>();
        lstAuthorities.add(new SimpleGrantedAuthority(token.getRole()));

        return new ASMUserDetails((String) auth.getPrincipal(), (String) auth.getCredentials(),
                                  lstAuthorities, token);

    }
}
