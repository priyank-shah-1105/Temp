package com.dell.asm.ui.spring;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class DataSessionTimeoutFilter extends OncePerRequestFilter {

    public static final String AJAX_DATA_LAST_ACCESS_TIME = "AjaxDataLastAccessTime";
    private static final Logger log = LoggerFactory.getLogger(DataSessionTimeoutFilter.class);
    private static final Pattern NO_TIMEOUT_PATTERN = Pattern.compile(
            "\\.html|discovery|configurechassis|downloads");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            if (request.getRequestURI() != null) {
                String url = request.getRequestURI();
                boolean noTimeout = false;
                if (url != null) {
                    Matcher matcher = NO_TIMEOUT_PATTERN.matcher(url.toLowerCase());
                    noTimeout = matcher.find();
                }
                if (noTimeout) {
                    session.removeAttribute(AJAX_DATA_LAST_ACCESS_TIME);
                } else {
                    Long lastAccess = (Long) session.getAttribute(AJAX_DATA_LAST_ACCESS_TIME);
                    if (lastAccess == null) {
                        lastAccess = System.currentTimeMillis();
                        session.setAttribute(AJAX_DATA_LAST_ACCESS_TIME,
                                             lastAccess);
                    } else {
                        long maxSessionLife = session.getMaxInactiveInterval() * 1000;
                        long elapsedTime = System.currentTimeMillis() - lastAccess;
                        long interval = maxSessionLife - elapsedTime;
                        if (interval < 0) {
                            log.debug(
                                    "Invalidating session " + session.getId() + " due to polling max session life: " + maxSessionLife + " elapsed time: " + elapsedTime + " interval: " + interval);
                            session.invalidate();
                        }
                    }
                }
            }
        }
        filterChain.doFilter(request,
                             response);
    }
}
