package com.switchhr.jsm.filter;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.ApplicationProperties;
import com.switchhr.jsm.service.PortalPermissionService;
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
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HRPortalAuthenticationFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HRPortalAuthenticationFilter.class);

    private PortalPermissionService permissionService;
    private ApplicationProperties applicationProperties;
    private JiraAuthenticationContext authenticationContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.permissionService = ComponentAccessor.getOSGiComponentInstanceOfType(PortalPermissionService.class);
        this.applicationProperties = ComponentAccessor.getOSGiComponentInstanceOfType(ApplicationProperties.class);
        this.authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String portalId = extractPortalId(httpRequest);
            ApplicationUser currentUser = authenticationContext != null ? authenticationContext.getLoggedInUser() : null;

            if (currentUser == null) {
                log.debug("User not authenticated, redirecting to login");
                redirectToLogin(httpRequest, httpResponse);
                return;
            }

            if (portalId != null && permissionService != null
                && !permissionService.hasPortalAccess(currentUser, portalId)) {
                log.warn("User {} denied access to portal {}", currentUser.getKey(), portalId);
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                    "{\"error\":\"Access denied\",\"message\":\"You do not have permission to access this portal\"}"
                );
                return;
            }

            httpRequest.setAttribute("currentUser", currentUser);
            httpRequest.setAttribute("portalId", portalId);

            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in authentication filter", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        // No-op
    }

    private String extractPortalId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/portal/")) {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length > 2) {
                return pathParts[2];
            }
        }
        return request.getParameter("portalId");
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (applicationProperties == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String baseUrl = applicationProperties.getBaseUrl();
        String currentUrl = request.getRequestURL().toString();
        if (request.getQueryString() != null) {
            currentUrl += "?" + request.getQueryString();
        }
        String loginUrl = baseUrl + "/login.jsp?os_destination=" +
            URLEncoder.encode(currentUrl, StandardCharsets.UTF_8);
        response.sendRedirect(loginUrl);
    }
}
