package org.farhan.mbt.maven;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public abstract class MBTMojo extends AbstractMojo {

    /**
     * The Maven Project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    public String baseDir = "";

    private final RestTemplate restTemplate;
    private HttpEntity<Void> requestEntity;
    private static final int RETRY_COUNT = 60;

    public MBTMojo() {
        baseDir = new File("").getAbsolutePath();
        RestClientConfig config = new RestClientConfig();
        restTemplate = config.restTemplate();
        requestEntity = new HttpEntity<>(new HttpHeaders());
    }

    @Parameter(property = "tags", defaultValue = "")
    public String tags;

    @Parameter(property = "host", defaultValue = "dev.sheepdog.io")
    public String host;

    @Parameter(property = "port", defaultValue = "80")
    public int port;

    @Parameter(property = "timeout", defaultValue = "300000")
    public int timeout;

    @Parameter(property = "onlyChanges", defaultValue = "false")
    public boolean onlyChanges;

    private String getHost() {
        return "http://" + host + ":" + port + "/";
    }

    private <T> T retry(Supplier<T> action, String operation) throws Exception {
        int retryCount = 0;
        while (retryCount < RETRY_COUNT) {
            try {
                return action.get();
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= RETRY_COUNT) {
                    getLog().error("Retry attempt " + retryCount + " for " + operation);
                    throw new Exception("Retry attempt " + retryCount + " for " + operation);
                }
                getLog().warn("Retry attempt " + retryCount + " for " + operation);
                Thread.sleep(10000);
            }
        }
        getLog().error("Max retries reached while " + operation);
        throw new Exception("Max retries reached while " + operation);
    }

    private void clearObjects(String resource, String goal) throws Exception {
        TreeMap<String, String> parameters = new TreeMap<String, String>();
        String url = getHost() + resource + "/clear" + goal + "Objects";
        if (!tags.isEmpty()) {
            parameters.put("tags", tags);
            url += "?tags={tags}";
        }
        String finalUrl = url;
        retry(() -> {
            restTemplate.exchange(finalUrl, HttpMethod.DELETE, requestEntity, Void.class, parameters);
            return null;
        }, "clearing objects for goal: " + goal);
    }

    private String convertObject(String resource, String goal, String fileName, String contents) throws Exception {
        TreeMap<String, String> parameters = new TreeMap<String, String>();
        String url = getHost() + resource + "/run" + goal + "?fileName={fileName}";
        if (!tags.isEmpty()) {
            parameters.put("tags", tags);
            url += "&tags={tags}";
        }
        parameters.put("fileName", fileName);
        String finalUrl = url;
        return retry(() -> {
            ResponseEntity<TransformableFile> postResponse = restTemplate.exchange(finalUrl, HttpMethod.POST,
                    new HttpEntity<>(contents, requestEntity != null ? requestEntity.getHeaders() : new HttpHeaders()),
                    TransformableFile.class, parameters);
            return postResponse.getBody().getFileContent();
        }, "converting object for goal: " + goal);
    }

    private List<TransformableFile> getFileChecksums(String resource, String goal) throws Exception {
        TreeMap<String, String> parameters = new TreeMap<String, String>();
        String url = getHost() + resource + "/get" + goal + "FileChecksums";
        if (!tags.isEmpty()) {
            parameters.put("tags", tags);
            url += "?tags={tags}";
        }
        String finalUrl = url;
        return retry(() -> {
            ResponseEntity<List<TransformableFile>> response = restTemplate.exchange(finalUrl, HttpMethod.GET,
                    requestEntity, new ParameterizedTypeReference<List<TransformableFile>>() {
                    }, parameters);
            return response.getBody();
        }, "getting file checksums for goal: " + goal);
    }

    private void deleteObject(String resource, String goal, String fileName) throws Exception {
        TreeMap<String, String> parameters = new TreeMap<String, String>();
        String url = getHost() + resource + "/delete" + goal + "Object?fileName={fileName}";
        if (!tags.isEmpty()) {
            parameters.put("tags", tags);
            url += "&tags={tags}";
        }
        parameters.put("fileName", fileName);
        String finalUrl = url;
        retry(() -> {
            restTemplate.exchange(finalUrl, HttpMethod.DELETE, requestEntity, Void.class, parameters);
            return null;
        }, "deleting object: " + fileName);
    }

    private String computeSHA1(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest((content == null ? "" : content).getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available", e);
        }
    }

    private List<TransformableFile> getObjectNames(String resource, String goal) throws Exception {
        TreeMap<String, String> parameters = new TreeMap<String, String>();
        String url = getHost() + resource + "/get" + goal + "ObjectNames";
        if (!tags.isEmpty()) {
            parameters.put("tags", tags);
            url += "?tags={tags}";
        }
        String finalUrl = url;
        return retry(() -> {
            ResponseEntity<List<TransformableFile>> response = restTemplate.exchange(finalUrl, HttpMethod.GET,
                    requestEntity, new ParameterizedTypeReference<List<TransformableFile>>() {
                    }, parameters);
            List<TransformableFile> fileList = response.getBody();
            for (TransformableFile tf : fileList) {
                getLog().info("ObjectName: " + tf.getFileName());
            }
            return fileList;
        }, "getting object names for goal: " + goal);
    }

    private void waitForService() throws MojoExecutionException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(getHost() + "sheep-dog-cucumber-gen-svc/actuator/health",
                        String.class);
                if (response.getStatusCode() == HttpStatus.OK && response.getBody().contains("\"status\":\"UP\"")) {
                    getLog().info("Service ready");
                    return;
                }
            } catch (Exception e) {
                long timeLeft = (timeout - (System.currentTimeMillis() - startTime)) / 1000;
                getLog().warn("Service not ready yet, " + timeLeft + " seconds remaining");
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MojoExecutionException("Interrupted while waiting for service", e);
            }
        }
        throw new MojoExecutionException("Service did not become available within " + timeout + " milliseconds");
    }

    public void execute(String resource, String goal) throws MojoExecutionException {
        execute(resource, goal, "");
    }

    public void execute(String resource, String goal, String extension) throws MojoExecutionException {
        getLog().info("Starting execute");
        getLog().info("tags: " + tags);
        getLog().info("baseDir: " + baseDir);

        if (!baseDir.endsWith("/")) {
            baseDir += "/";
        }
        SourceFileRepository sr = new SourceFileRepository(baseDir);
        // TODO get the layer 1 directory from the converter
        String[] dirs = { "src/test/resources/asciidoc/", "src-gen/test/resources/cucumber/" };
        try {
            waitForService();
            if (goal.endsWith("ToUML")) {
                if (onlyChanges) {
                    executeIncremental(resource, goal, extension, sr, dirs);
                } else {
                    clearObjects(resource, goal);
                    for (String dir : dirs) {
                        ArrayList<String> tempFiles = new ArrayList<String>();
                        for (String fileName : sr.list("", dir, extension)) {
                            if (fileName.startsWith(dir + "stepdefs")) {
                                tempFiles.add(fileName);
                            } else {
                                getLog().info("Converting: " + fileName);
                                convertObject(resource, goal, fileName, sr.get("", fileName));
                            }
                        }
                        for (String fileName : tempFiles) {
                            getLog().info("Converting: " + fileName);
                            convertObject(resource, goal, fileName, sr.get("", fileName));
                        }
                    }
                }
            } else {
                for (TransformableFile tf : getObjectNames(resource, goal)) {
                    String content = convertObject(resource, goal, tf.getFileName(),
                            sr.contains("", tf.getFileName()) ? sr.get("", tf.getFileName()) : null);
                    if (!content.isEmpty()) {
                        getLog().info("Converting: " + tf.getFileName());
                        sr.put("", tf.getFileName(), content);
                    } else {
                        getLog().info("Skipping: " + tf.getFileName());
                    }
                }
            }
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
            throw new MojoExecutionException(e);
        }
        getLog().info("Ending execute");
    }

    private void executeIncremental(String resource, String goal, String extension, SourceFileRepository sr,
            String[] dirs) throws Exception {
        // Get remote checksums
        Map<String, String> remoteChecksums = new HashMap<>();
        for (TransformableFile tf : getFileChecksums(resource, goal)) {
            remoteChecksums.put(tf.getFileName(), tf.getFileChecksum());
        }
        getLog().info("Remote files with checksums: " + remoteChecksums.size());

        // Compute local checksums and categorize files
        Map<String, String> localChecksums = new HashMap<>();
        List<String> nonTempToUpload = new ArrayList<>();
        List<String> tempToUpload = new ArrayList<>();
        int skipped = 0;
        int creating = 0;
        int updating = 0;

        for (String dir : dirs) {
            for (String fileName : sr.list("", dir, extension)) {
                String content = sr.get("", fileName);
                String localChecksum = computeSHA1(content);
                localChecksums.put(fileName, localChecksum);
                String remoteChecksum = remoteChecksums.get(fileName);

                if (localChecksum.equals(remoteChecksum)) {
                    getLog().info("Skipping: " + fileName);
                    skipped++;
                } else {
                    if (fileName.startsWith(dir + "stepdefs")) {
                        tempToUpload.add(fileName);
                    } else {
                        nonTempToUpload.add(fileName);
                    }
                    if (remoteChecksum == null) {
                        getLog().info("Creating: " + fileName);
                        creating++;
                    } else {
                        getLog().info("Updating: " + fileName);
                        updating++;
                    }
                }
            }
        }

        // Determine deletions (files on remote but not local)
        List<String> toDelete = new ArrayList<>();
        for (String remoteFileName : remoteChecksums.keySet()) {
            if (!localChecksums.containsKey(remoteFileName)) {
                toDelete.add(remoteFileName);
                getLog().info("Deleting: " + remoteFileName);
            }
        }

        if (nonTempToUpload.isEmpty() && tempToUpload.isEmpty() && toDelete.isEmpty()) {
            getLog().info("No changes detected, skipping entire upload cycle");
            return;
        }

        // Upload changed/new files (non-stepdefs first, then stepdefs)
        for (String fileName : nonTempToUpload) {
            convertObject(resource, goal, fileName, sr.get("", fileName));
        }
        for (String fileName : tempToUpload) {
            convertObject(resource, goal, fileName, sr.get("", fileName));
        }

        // Delete removed files
        for (String fileName : toDelete) {
            deleteObject(resource, goal, fileName);
        }

        getLog().info("Remote: " + remoteChecksums.size());
        getLog().info("Skipping: " + skipped);
        getLog().info("Creating: " + creating);
        getLog().info("Updating: " + updating);
        getLog().info("Deleting: " + toDelete.size());
    }

    public void setScenarioId(String scenarioId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("scenarioId", scenarioId);
        requestEntity = new HttpEntity<>(httpHeaders);
    }

}
