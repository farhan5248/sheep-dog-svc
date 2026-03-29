package org.farhan.common;

import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.farhan.runners.surefire.springboot.CucumberTestConfig;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CaptureInterceptor implements ClientHttpRequestInterceptor {
    private static int counter = 0;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String requestBody = new String(body, StandardCharsets.UTF_8);
        ClientHttpResponse response = execution.execute(request, body);
        byte[] responseBodyBytes = response.getBody().readAllBytes();
        String responseBody = new String(responseBodyBytes, StandardCharsets.UTF_8);

        saveGroovyContract(
                "src/test/resources/contracts/" + CucumberTestConfig.scenarioId + "." + counter + ".groovy",
                request, requestBody, response, responseBody);

        // Return a new response with the buffered body
        return new BufferingClientHttpResponseWrapper(response, responseBodyBytes);
    }

    private void saveGroovyContract(String path, HttpRequest request, String requestBody, ClientHttpResponse response,
            String responseBody) throws IOException {
        String method = request.getMethod().name();
        String url = request.getURI().getPath();
        @SuppressWarnings("deprecation")
        int status = response.getRawStatusCode();
        String contentType = response.getHeaders().getContentType() != null
                ? response.getHeaders().getContentType().toString()
                : null;
        StringBuilder groovy = new StringBuilder();
        groovy.append("org.springframework.cloud.contract.spec.Contract.make {\n");
        groovy.append("    request {\n");
        groovy.append("        method '" + method + "'\n");
        // Add query parameters if present (map style)
        String query = request.getURI().getQuery();
        if (query != null && !query.isBlank()) {
            groovy.append("        url ('" + url + "') {\n");
            groovy.append("            queryParameters {\n");
            String[] params = query.split("&");
            for (String param : params) {
                String[] kv = param.split("=", 2);
                String key = kv[0];
                if (kv[1].isBlank()) {
                    continue;
                }
                groovy.append("                parameter " + key + ": '" + kv[1] + "'\n");
            }
            groovy.append("            }\n");
            groovy.append("        }\n");

        } else {
            groovy.append("        url '" + url + "'\n");
        }
        if (requestBody != null && !requestBody.isBlank()) {
            groovy.append("        body(file('" + "bodies/" + CucumberTestConfig.scenarioId + "." + counter
                    + ".req.txt" + "'))\n");
        }
        if (CucumberTestConfig.scenarioId != null) {
            groovy.append("        headers {\n");
            groovy.append("            header('scenarioId', '" + CucumberTestConfig.scenarioId + "')\n");
            groovy.append("        }\n");
        }
        groovy.append("    }\n");
        groovy.append("    response {\n");
        groovy.append("        status " + status + "\n");
        if (contentType != null) {
            groovy.append("        headers {\n");
            groovy.append("            contentType('" + contentType + "')\n");
            groovy.append("        }\n");
        }
        if (responseBody != null && !responseBody.isBlank()) {
            groovy.append("        body(file('" + "bodies/" + CucumberTestConfig.scenarioId + "." + counter
                    + ".rsp.json" + "'))\n");
        }
        groovy.append("    }\n");
        groovy.append("}\n");

        File bodyDir = new File("src/test/resources/contracts/bodies");
        bodyDir.mkdirs(); // Ensure the directory exists

        if (requestBody != null && !requestBody.isBlank()) {
            FileWriter fw = new FileWriter(
                    "src/test/resources/contracts/bodies/" + CucumberTestConfig.scenarioId + "." + counter
                            + ".req.txt",
                    false);
            fw.write(requestBody);
            fw.close();
        }

        if (responseBody != null && !responseBody.isBlank()) {
            FileWriter fw = new FileWriter(
                    "src/test/resources/contracts/bodies/" + CucumberTestConfig.scenarioId + "." + counter
                            + ".rsp.json",
                    false);
            fw.write(responseBody.replace("\\\\", "\\\\\\\\"));
            fw.close();
        }

        String newContract = groovy.toString();
        try (FileWriter fw = new FileWriter(path, false)) {
            fw.write(newContract);
            counter++;
        }

    }

    // Wrapper to allow multiple reads of the response body
    private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse response;
        private final byte[] body;

        BufferingClientHttpResponseWrapper(ClientHttpResponse response, byte[] body) {
            this.response = response;
            this.body = body;
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public org.springframework.http.HttpHeaders getHeaders() {
            return response.getHeaders();
        }

        @Override
        public org.springframework.http.HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @Override
        public int getRawStatusCode() throws IOException {
            // Deprecated in Spring 6+, but still required for interface
            return response.getRawStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return response.getStatusText();
        }

        @Override
        public void close() {
            response.close();
        }
    }

}
