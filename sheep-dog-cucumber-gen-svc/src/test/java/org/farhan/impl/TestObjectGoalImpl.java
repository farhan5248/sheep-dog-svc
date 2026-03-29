package org.farhan.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.farhan.mbt.model.TransformableFile;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TestObjectGoalImpl extends TestObjectSheepDogImpl {
	private static final Logger logger = LoggerFactory.getLogger(TestObjectGoalImpl.class);
	private final RestTemplate restTemplate;
	// TODO make this a property
	private final int RETRY_COUNT = 60;

	public TestObjectGoalImpl() {
		setProperty("tags", "");
		restTemplate = new RestTemplate();
		// TODO add a property for this
		// restTemplate.getInterceptors().add(new CaptureInterceptor());
	}

	@Value("${sheepdog.host:dev.sheepdogdev.io}")
	private String serverHost;

	@Value("${sheepdog.port:80}")
	private int serverPort;

	@Value("${sheepdog.timeout:120000}")
	private int timeout;

	private String getHost() {
		return "http://" + serverHost + ":" + serverPort + "/";
	}

	private void clearObjects(String resource, String goal) throws Exception {
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("tags", getProperty("tags").toString());
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				restTemplate.delete(getHost() + resource + "/clear" + goal + "Objects?tags={tags}", parameters);
				return; // Exit if successful
			} catch (Exception e) {
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while clearing objects for goal: " + goal);
	}

	private String convertObject(String resource, String goal, String fileName, String contents) throws Exception {
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("tags", getProperty("tags").toString());
		parameters.put("fileName", fileName);
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				return restTemplate
						.postForObject(getHost() + resource + "/run" + goal + "?tags={tags}&fileName={fileName}",
								contents, TransformableFile.class, parameters)
						.getFileContent();
			} catch (Exception e) {
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while converting objects for goal: " + goal);
	}

	private List<TransformableFile> getObjectNames(String resource, String goal) throws Exception {

		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("tags", getProperty("tags").toString());
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				ResponseEntity<List<TransformableFile>> response = restTemplate.exchange(
						getHost() + resource + "/get" + goal + "ObjectNames?tags={tags}", HttpMethod.GET, null,
						new ParameterizedTypeReference<List<TransformableFile>>() {
						}, parameters);
				List<TransformableFile> fileList = response.getBody();
				return fileList;
			} catch (Exception e) {
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while getting object names for goal: " + goal);
	}

	private void waitForService() throws Exception {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < timeout) {
			try {
				ResponseEntity<String> response = restTemplate.getForEntity(getHost() + "actuator/health",
						String.class);
				if (response.getStatusCode() == HttpStatus.OK && response.getBody().contains("\"status\":\"UP\"")) {
					logger.info("Service ready");
					return;
				}
			} catch (Exception e) {
				long timeLeft = (timeout - (System.currentTimeMillis() - startTime)) / 1000;
				logger.warn("Service not ready yet, " + timeLeft + " seconds remaining");
			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new Exception("Interrupted while waiting for service", e);
			}
		}

		throw new Exception("Service did not become available within " + timeout + " milliseconds");
	}

	protected void runGoal(String resource, String goal) {

		SourceFileRepositoryImpl sr = new SourceFileRepositoryImpl();
		String[] dirs = { "src/test/resources/asciidoc/", "src-gen/test/resources/cucumber/" };
		try {
			waitForService();
			if (goal.endsWith("ToUML")) {
				clearObjects(resource, goal);
				for (String dir : dirs) {
					ArrayList<String> tempFiles = new ArrayList<String>();
					for (String fileName : sr.list("", dir, "")) {
						if (fileName.startsWith(dir + "stepdefs")) {
							tempFiles.add(fileName);
						} else {
							convertObject(resource, goal, fileName, sr.get("", fileName));
						}
					}
					for (String fileName : tempFiles) {
						convertObject(resource, goal, fileName, sr.get("", fileName));
					}
				}
			} else {
				for (TransformableFile tf : getObjectNames(resource, goal)) {
					String content = convertObject(resource, goal, tf.getFileName(),
							sr.contains("", tf.getFileName()) ? sr.get("", tf.getFileName()) : null);
					if (!content.isEmpty()) {
						sr.put("", tf.getFileName(), content);
					}
				}
			}
		} catch (Exception e) {
			Assertions.fail(e);
		}
	}
}
