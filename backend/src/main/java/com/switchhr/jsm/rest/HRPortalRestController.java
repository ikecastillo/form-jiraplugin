package com.switchhr.jsm.rest;

import com.atlassian.jira.user.ApplicationUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchhr.jsm.config.PortalConfigurationDTO;
import com.switchhr.jsm.model.PortalConfiguration;
import com.switchhr.jsm.service.JiraServiceDeskIntegrationService;
import com.switchhr.jsm.service.PortalConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HRPortalRestController {

    private final PortalConfigurationService configurationService;
    private final JiraServiceDeskIntegrationService jiraService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public HRPortalRestController(PortalConfigurationService configurationService,
                                 JiraServiceDeskIntegrationService jiraService) {
        this.configurationService = configurationService;
        this.jiraService = jiraService;
    }

    @GET
    @Path("/portals/{portalId}/config")
    public Response getPortalConfiguration(@PathParam("portalId") String portalId) {
        try {
            Optional<PortalConfiguration> configOpt = configurationService.getPortalConfiguration(portalId);
            if (configOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Portal configuration not found"))
                    .build();
            }

            String configJson = configOpt.get().getConfigurationJson();
            JsonNode configNode = objectMapper.readTree(configJson);
            return Response.ok(configNode).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Failed to retrieve configuration"))
                .build();
        }
    }

    @PUT
    @Path("/portals/{portalId}/config")
    public Response updatePortalConfiguration(@PathParam("portalId") String portalId,
                                              PortalConfigurationDTO configuration,
                                              @Context HttpServletRequest request) {
        try {
            ApplicationUser currentUser = (ApplicationUser) request.getAttribute("currentUser");
            if (currentUser == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Authentication required"))
                    .build();
            }

            String configJson = objectMapper.writeValueAsString(configuration);
            configurationService.updatePortalConfiguration(portalId, configJson, currentUser.getKey());

            return Response.ok(Map.of("success", true, "message", "Configuration updated"))
                .build();
        } catch (ValidationException ve) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Validation failed", "details", ve.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Failed to update configuration"))
                .build();
        }
    }

    @GET
    @Path("/jira/service-desks/{serviceDeskId}/request-types")
    public Response getRequestTypes(@PathParam("serviceDeskId") String serviceDeskId,
                                    @QueryParam("search") String search) {
        try {
            List<Map<String, Object>> requestTypes = jiraService.getRequestTypes(serviceDeskId, search);
            return Response.ok(requestTypes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Failed to retrieve request types"))
                .build();
        }
    }

    @GET
    @Path("/jira/service-desks/{serviceDeskId}/request-types/{requestTypeId}/fields")
    public Response getRequestTypeFields(@PathParam("serviceDeskId") String serviceDeskId,
                                         @PathParam("requestTypeId") String requestTypeId) {
        try {
            List<Map<String, Object>> fields = jiraService
                .getRequestTypeFieldsAsync(serviceDeskId, requestTypeId)
                .join();
            return Response.ok(fields).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Failed to retrieve request type fields"))
                .build();
        }
    }

    @POST
    @Path("/jira/requests")
    public Response createServiceRequest(Map<String, Object> requestData,
                                         @Context HttpServletRequest request) {
        try {
            ApplicationUser currentUser = (ApplicationUser) request.getAttribute("currentUser");
            String portalId = (String) request.getAttribute("portalId");

            String issueKey = jiraService.createServiceRequest(requestData, currentUser, portalId);
            return Response.status(Response.Status.CREATED)
                .entity(Map.of("success", true, "issueKey", issueKey))
                .build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", "Failed to create request"))
                .build();
        }
    }
}
