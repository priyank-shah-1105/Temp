package com.dell.asm.ui.spring;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.dell.asm.asmcore.user.model.AuthenticateResponse;

/**
 * Adds rest API security key and secret.
 */
public class ASMUserDetails extends User {

    /**
     * Generated.
     */
    private static final long serialVersionUID = -7451040421305712349L;
    private AuthenticateResponse m_token;

    /**
     * Calls the more complex constructor with all boolean arguments set to {@code true}.
     * Default parameters to more complex User constructor are set to true.
     *
     * @param username    the username presented to the
     *                    <code>DaoAuthenticationProvider</code>
     * @param password    the password that should be presented to the
     *                    <code>DaoAuthenticationProvider</code>
     * @param authorities the authorities that should be granted to the caller
     *                    if they presented the correct username and password and the user
     *                    is enabled. Not null.
     * @param restToken   RST API key and secret
     */
    public ASMUserDetails(String username,
                          String password,
                          Collection<? extends GrantedAuthority> authorities,
                          AuthenticateResponse restToken) {
        super(username,
              password,
              true,
              true,
              true,
              true,
              authorities);

        m_token = restToken;
    }

    /**
     * Accessor for REST API key and secret.
     * @return
     */
    public AuthenticateResponse getAuthToken() {
        return m_token;
    }

    /**
     * Returns {@code true} if the supplied object is a {@code ASMUserDetails} instance with the
     * same apiKey and apiSecret value.
     */
    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof ASMUserDetails
                && m_token != null
                && ((ASMUserDetails) rhs).getAuthToken() != null)
            return m_token.getApiSecret().equals(
                    ((ASMUserDetails) rhs).getAuthToken().getApiSecret())
                    && m_token.getApiSecret().equals(
                    ((ASMUserDetails) rhs).getAuthToken().getApiSecret());
        else
            return super.equals(rhs);
    }

    /**
     * Returns the hashcode of the apiSecret.
     */
    @Override
    public int hashCode() {
        if (m_token != null
                && m_token.getApiSecret() != null)
            return m_token.getApiSecret().hashCode();
        else
            return super.hashCode();
    }

}
