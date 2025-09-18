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

        Map<String, Object> context = new HashMap<>();
        context.put("baseUrl", applicationProperties.getBaseUrl());
        context.put("resourceKey", RESOURCE_KEY);
        context.put("mountNodeId", MOUNT_NODE_ID);

        templateRenderer.render(TEMPLATE_PATH, context, resp.getWriter());
    }
}
