package com.dell.asm.ui.authorization;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Component;

/**
 * The Class ASMUILoginUrlAuthenticationEntryPoint.
 */
@Component("asmuiRestAuthenticationEntryPoint")
public class ASMUIRestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(ASMUIRestAuthenticationEntryPoint.class);

    /**
     * The redirect strategy.
     */
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    /**
     * Performs the redirect (or forward) to the login form URL.
     *
     * @param request       the request
     * @param response      the response
     * @param authException the auth exception
     * @throws IOException      Signals that an I/O exception has occurred.
     * @throws ServletException the servlet exception
     */
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        if (authException != null) {
            log.debug("Authentication Exception has been received");
            if (request.getRequestURI().contains(".html")) {
                redirectStrategy.sendRedirect(request, response, "/");
            } else {
                request.setAttribute(WebAttributes.ACCESS_DENIED_403, authException.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
    }

}
