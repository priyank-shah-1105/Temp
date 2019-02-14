package com.dell.asm.ui.authorization;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.dell.asm.ui.controller.ServersController;

/**
 * The Class VISLogoutSuccessHandler.
 */

@Component("asmuiLogoutSuccessHandler")
public class ASMUILogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private static final Logger log = Logger.getLogger(ServersController.class);

    /**
     * @see org.springframework.security.web.authentication.logout
     * .SimpleUrlLogoutSuccessHandler#onLogoutSuccess(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse,
     *      org.springframework.security.core.Authentication)
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication)
            throws IOException {
        log.debug("Clearing the Context and Creating an empty one");
        SecurityContextHolder.clearContext();
        SecurityContextHolder.createEmptyContext();
    }
}
