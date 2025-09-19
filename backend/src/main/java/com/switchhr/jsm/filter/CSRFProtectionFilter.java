package com.switchhr.jsm.filter;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

public class CSRFProtectionFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CSRFProtectionFilter.class);
    private static final String CSRF_TOKEN_HEADER = "X-CSRF-Token";
    private static final String CSRF_TOKEN_PARAM = "csrf_token";
    private static final String CSRF_TOKEN_SESSION_ATTR = "csrf_token";

    private final SecureRandom secureRandom = new SecureRandom();
    private JiraAuthenticationContext authenticationContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String method = httpRequest.getMethod();
            String csrfToken = getOrCreateCSRFToken(httpRequest);
            httpResponse.setHeader(CSRF_TOKEN_HEADER, csrfToken);

            if (isStateChangingMethod(method) && !validateCSRFToken(httpRequest, csrfToken)) {
                ApplicationUser currentUser = authenticationContext != null ? authenticationContext.getLoggedInUser() : null;
                log.warn("CSRF token validation failed for request from {} (user={})",
                    httpRequest.getRemoteAddr(), currentUser != null ? currentUser.getName() : "anonymous");

                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                    "{\"error\":\"CSRF validation failed\",\"message\":\"Invalid or missing CSRF token\"}"
                );
                return;
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in CSRF protection filter", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        // No-op
    }

    private boolean isStateChangingMethod(String method) {
        return "POST".equalsIgnoreCase(method) ||
            "PUT".equalsIgnoreCase(method) ||
            "DELETE".equalsIgnoreCase(method) ||
            "PATCH".equalsIgnoreCase(method);
    }

    private String getOrCreateCSRFToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        if (token == null) {
            token = generateCSRFToken();
            session.setAttribute(CSRF_TOKEN_SESSION_ATTR, token);
        }
        return token;
    }

    private String generateCSRFToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean validateCSRFToken(HttpServletRequest request, String expectedToken) {
        if (expectedToken == null) {
            return false;
        }
        String providedToken = request.getHeader(CSRF_TOKEN_HEADER);
        if (providedToken == null) {
            providedToken = request.getParameter(CSRF_TOKEN_PARAM);
        }
        return expectedToken.equals(providedToken);
    }
}
