package com.aegis.bridge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class EmergencyOracleClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String oracleUrl;
    private final String oracleKey;
    private final boolean required;
    private final HttpClient client;

    public EmergencyOracleClient(String oracleUrl, String oracleKey, boolean required) {
        this.oracleUrl = oracleUrl != null ? oracleUrl.replaceAll("/+$", "") : null;
        this.oracleKey = oracleKey;
        this.required = required;
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();
    }

    public void assertFinancialExecutionAllowed() {
        if (!required && (oracleUrl == null || oracleUrl.isBlank())) {
            return;
        }

        if (oracleUrl == null || oracleUrl.isBlank()) {
            throw new IllegalStateException("AEGIS: EMERGENCY_ORACLE_UNAVAILABLE (oracle URL not configured)");
        }

        try {
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(oracleUrl + "/oracle/state"))
                .timeout(Duration.ofSeconds(2))
                .GET();

            if (oracleKey != null && !oracleKey.isBlank()) {
                reqBuilder.header("X-Aegis-Oracle-Key", oracleKey);
            }

            HttpResponse<String> response = client.send(
                reqBuilder.build(),
                HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                    "AEGIS: EMERGENCY_ORACLE_UNAVAILABLE (HTTP " + response.statusCode() + ")"
                );
            }

            JsonNode root = MAPPER.readTree(response.body());
            String state = root.path("state").asText("");
            String reason = root.path("reason").asText("");

            if ("EMERGENCY_STOP".equalsIgnoreCase(state)) {
                throw new IllegalStateException("AEGIS: EMERGENCY_STOP_ACTIVE");
            }

            if ("DEGRADED".equalsIgnoreCase(state)) {
                System.out.println("WARN: Emergency Oracle state is DEGRADED: " + reason);
                return;
            }

            if ("NORMAL".equalsIgnoreCase(state)) {
                return;
            }

            throw new IllegalStateException("AEGIS: EMERGENCY_STOP_ACTIVE (UNKNOWN_ORACLE_STATE: " + state + ")");

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("AEGIS: EMERGENCY_ORACLE_UNAVAILABLE (" + e.getMessage() + ")", e);
        }
    }
}
