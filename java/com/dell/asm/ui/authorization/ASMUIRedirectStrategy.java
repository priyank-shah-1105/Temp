package com.dell.asm.ui.authorization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.RedirectStrategy;

/**
 * The Class ASMUIRedirectStrategy.
 */
public class ASMUIRedirectStrategy implements RedirectStrategy {

    /**
     * @see org.springframework.security.web.RedirectStrategy#sendRedirect(javax.servlet.http
     * .HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.String)
     */
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) {

    }
}
