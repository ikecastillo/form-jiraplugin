package com.switchhr.jsm.service;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.ApplicationProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class JiraServiceDeskIntegrationService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApplicationProperties applicationProperties;

    public JiraServiceDeskIntegrationService() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.applicationProperties = ComponentAccessor.getOSGiComponentInstanceOfType(ApplicationProperties.class);
    }

    public List<Map<String, Object>> getRequestTypes(String serviceDeskId, String search) {
        try {
            String endpoint = String.format("/rest/servicedeskapi/servicedesk/%s/requesttype", serviceDeskId);
            if (search != null && !search.trim().isEmpty()) {
                endpoint += "?searchQuery=" + URLEncoder.encode(search, StandardCharsets.UTF_8);
            }

            HttpResponse<String> response = makeJiraApiRequest(endpoint, "GET", null);
            if (response.statusCode() == 200) {
                Map<String, Object> responseData = objectMapper.readValue(response.body(),
                    new TypeReference<Map<String, Object>>() {});
                Object values = responseData.getOrDefault("values", Collections.emptyList());
                return objectMapper.convertValue(values, new TypeReference<List<Map<String, Object>>>() {});
            }
            throw new IllegalStateException("Failed to fetch request types: HTTP " + response.statusCode());
        } catch (Exception e) {
            throw new IllegalStateException("Error fetching request types", e);
        }
    }

    public String createServiceRequest(Map<String, Object> requestData,
                                       ApplicationUser user,
                                       String portalId) {
        try {
            Map<String, Object> payload = buildServiceRequestPayload(requestData, user, portalId);
            HttpResponse<String> response = makeJiraApiRequest(
                "/rest/servicedeskapi/request",
                "POST",
                objectMapper.writeValueAsString(payload));

            if (response.statusCode() == 201) {
                Map<String, Object> responseData = objectMapper.readValue(response.body(),
                    new TypeReference<Map<String, Object>>() {});
                return (String) responseData.get("issueKey");
            }
            throw new IllegalStateException("Failed to create service request: HTTP " + response.statusCode());
        } catch (Exception e) {
            throw new IllegalStateException("Error creating service request", e);
        }
    }

    public CompletableFuture<List<Map<String, Object>>> getRequestTypeFieldsAsync(String serviceDeskId,
                                                                                 String requestTypeId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String endpoint = String.format(
                    "/rest/servicedeskapi/servicedesk/%s/requesttype/%s/field",
                    serviceDeskId, requestTypeId);
                HttpResponse<String> response = makeJiraApiRequest(endpoint, "GET", null);

                if (response.statusCode() == 200) {
                    Map<String, Object> responseData = objectMapper.readValue(response.body(),
                        new TypeReference<Map<String, Object>>() {});
                    Object fields = responseData.getOrDefault("requestTypeFields", Collections.emptyList());
                    return objectMapper.convertValue(fields, new TypeReference<List<Map<String, Object>>>() {});
                }
                throw new IllegalStateException("Failed to fetch request type fields: HTTP " + response.statusCode());
            } catch (Exception e) {
                throw new IllegalStateException("Error fetching request type fields", e);
            }
        });
    }

    private Map<String, Object> buildServiceRequestPayload(Map<String, Object> requestData,
                                                          ApplicationUser user,
                                                          String portalId) {
        Map<String, Object> payload = new HashMap<>();
        if (requestData == null) {
            return payload;
        }
        payload.put("serviceDeskId", requestData.get("serviceDeskId"));
        payload.put("requestTypeId", requestData.get("requestTypeId"));

        Map<String, Object> formData = objectMapper.convertValue(requestData.get("formData"), new TypeReference<Map<String, Object>>() {});
        Map<String, Object> fieldValues = new HashMap<>();

        if (formData != null) {
            if (formData.containsKey("summary")) {
                fieldValues.put("summary", formData.get("summary"));
            }
            if (formData.containsKey("description")) {
                fieldValues.put("description", formData.get("description"));
            }
        }

        payload.put("requestFieldValues", fieldValues);
        payload.put("raiseOnBehalfOf", user != null ? user.getUsername() : null);
        payload.put("portalId", portalId);

        if (requestData.containsKey("requestParticipants")) {
            payload.put("requestParticipants", requestData.get("requestParticipants"));
        }
        return payload;
    }

    private HttpResponse<String> makeJiraApiRequest(String endpoint,
                                                   String method,
                                                   String body) throws IOException, InterruptedException {
        String baseUrl = applicationProperties.getBaseUrl();
        URI uri = URI.create(baseUrl + endpoint);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofSeconds(30))
            .header("Accept", "application/json")
            .header("X-Atlassian-Token", "no-check");

        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            requestBuilder.header("Content-Type", "application/json");
            requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
        } else {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    }
}

