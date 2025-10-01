package com.switchhr.jsm.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.jira.user.ApplicationUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PortalSettingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String TEMPLATE_PATH = "templates/portal-settings.vm";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        TemplateRenderer templateRenderer = ComponentAccessor.getOSGiComponentInstanceOfType(TemplateRenderer.class);
        if (templateRenderer == null) {
            throw new IllegalStateException("TemplateRenderer service is not available");
        }

        ApplicationProperties applicationProperties = ComponentAccessor.getOSGiComponentInstanceOfType(ApplicationProperties.class);
        if (applicationProperties == null) {
            throw new IllegalStateException("ApplicationProperties service is not available");
        }

        JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
        ApplicationUser loggedInUser = authenticationContext != null ? authenticationContext.getLoggedInUser() : null;
        String displayName = loggedInUser != null ? loggedInUser.getDisplayName() : "Anonymous";

        String spaceKey = extractSpaceKey(req);

        Map<String, Object> context = new HashMap<>();
        context.put("baseUrl", applicationProperties.getBaseUrl());
        context.put("currentUser", displayName);
        context.put("spaceKey", spaceKey);

        templateRenderer.render(TEMPLATE_PATH, context, resp.getWriter());
    }

    private String extractSpaceKey(HttpServletRequest req) {
        String spaceKey = req.getParameter("spaceKey");
        if (spaceKey == null || spaceKey.trim().isEmpty()) {
            spaceKey = req.getParameter("projectKey");
        }
        if (spaceKey == null || spaceKey.trim().isEmpty()) {
            spaceKey = "Unknown";
        }
        return spaceKey;
    }
}
