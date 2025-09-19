package com.switchhr.jsm.servlet;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.sal.api.ApplicationProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HRPortalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String TEMPLATE_PATH = "templates/hr-portal.vm";
    private static final String RESOURCE_KEY = "com.switchhr.jsm.hrportal:hr-portal-resources";
    private static final String MOUNT_NODE_ID = "hr-portal-root";
    private static final String DEFAULT_PORTAL_ID = "hr-portal";

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

        String portalId = resolvePortalId(req);

        Map<String, Object> context = new HashMap<>();
        context.put("baseUrl", applicationProperties.getBaseUrl());
        context.put("resourceKey", RESOURCE_KEY);
        context.put("mountNodeId", MOUNT_NODE_ID);
        context.put("portalId", portalId);

        templateRenderer.render(TEMPLATE_PATH, context, resp.getWriter());
    }

    private String resolvePortalId(HttpServletRequest request) {
        String portalId = request.getParameter("portalId");
        if (portalId == null || portalId.trim().isEmpty()) {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null) {
                String[] parts = pathInfo.split("/");
                if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) {
                    portalId = parts[parts.length - 1];
                }
            }
        }
        if (portalId == null || portalId.trim().isEmpty()) {
            portalId = DEFAULT_PORTAL_ID;
        }
        return portalId;
    }
}
