package com.dell.asm.ui.adapter.service;

import org.springframework.security.core.AuthenticationException;

import com.dell.asm.asmcore.user.model.AuthenticateResponse;

/**
 * The Interface UserServiceAdapter.
 */
public interface UserServiceAdapter {

    /**
     * Login to NBI API.
     *
     * @param userName the user name
     * @param password the password
     * @param domain default local
     * @return token with apiKey and apiSecret.
     */
    AuthenticateResponse login(String userName, String password,
                               String domain) throws AuthenticationException;

}
