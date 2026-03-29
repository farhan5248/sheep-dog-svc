/*
* Copyright 2024 - 2024 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.farhan.mbt;

import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CucumberService {

	Logger logger = LoggerFactory.getLogger(CucumberService.class);
	private static String BASE_URL;
	private final int RETRY_COUNT = 10;
	private final RestClient restClient;

	@Autowired
	public CucumberService(@Value("${sheepdog.host:sheep-dog-dev-svc}") String host,
			@Value("${sheepdog.port:80}") int port) {
		BASE_URL = "http://" + host + ":" + port;
		this.restClient = RestClient.builder()
				.baseUrl(BASE_URL)
				.build();
	}

	/**
	 * Clear objects for Cucumber to UML conversion
	 *
	 * @param tags Test case tags, can be blank or empty
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Clear objects for Cucumber files", description = "Clear objects for Cucumber files in the UML model")
	public void clearConvertCucumberToUMLObjects(String tags) throws Exception {
		logger.info("Clearing objects for Cucumber files with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		String uri = "/cucumber/clearConvertCucumberToUMLObjects";
		if (!tags.isEmpty()) {
			parameters.put("tags", tags);
			uri += "?tags={tags}";
		}
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				restClient.delete().uri(uri, parameters).retrieve().body(Void.class);
				return;
			} catch (Exception e) {
				logger.error("Retry attempt " + (retryCount + 1), e);
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while clearing objects for Cucumber files");
	}

	/**
	 * Convert Cucumber file to UML model
	 *
	 * @param tags     Test case tags, can be blank or empty
	 * @param fileName The name of the file to convert
	 * @param contents The contents of the file to convert
	 * @return The converted file content
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Convert Cucumber to UML", description = "Convert a Cucumber feature file to the UML model")
	public String runConvertCucumberToUML(String tags, String fileName, String contents) throws Exception {
		logger.info("Converting Cucumber file to UML: " + fileName + " with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("fileName", fileName);
		String uri = "/cucumber/runConvertCucumberToUML?fileName={fileName}";
		if (!tags.isEmpty()) {
			parameters.put("tags", tags);
			uri += "&tags={tags}";
		}
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				return restClient.post().uri(uri, parameters).body(contents).retrieve().body(String.class);
			} catch (Exception e) {
				logger.error("Retry attempt " + (retryCount + 1), e);
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while converting Cucumber file to UML");
	}

	/**
	 * Convert UML model to Cucumber file
	 *
	 * @param tags     Test case tags, can be blank or empty
	 * @param fileName The name of the file to convert
	 * @param contents The existing contents of the file (can be empty)
	 * @return The converted file content
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Convert UML to Cucumber", description = "Convert a UML model element to a standard Cucumber feature file")
	public String runConvertUMLToCucumber(String tags, String fileName, String contents) throws Exception {
		logger.info("Converting UML to Cucumber file: " + fileName + " with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("fileName", fileName);
		String uri = "/cucumber/runConvertUMLToCucumber?fileName={fileName}";
		if (!tags.isEmpty()) {
			parameters.put("tags", tags);
			uri += "&tags={tags}";
		}
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				return restClient.post().uri(uri, parameters).body(contents).retrieve().body(String.class);
			} catch (Exception e) {
				logger.error("Retry attempt " + (retryCount + 1), e);
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while converting UML to Cucumber file");
	}

	/**
	 * Convert UML model to Cucumber Guice file
	 *
	 * @param tags     Test case tags, can be blank or empty
	 * @param fileName The name of the file to convert
	 * @param contents The existing contents of the file (can be empty)
	 * @return The converted file content
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Convert UML to Cucumber Guice", description = "Convert a UML model element to a Guice-based Cucumber file")
	public String runConvertUMLToCucumberGuice(String tags, String fileName, String contents) throws Exception {
		logger.info("Converting UML to Cucumber Guice file: " + fileName + " with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("fileName", fileName);
		String uri = "/cucumber/runConvertUMLToCucumberGuice?fileName={fileName}";
		if (!tags.isEmpty()) {
			parameters.put("tags", tags);
			uri += "&tags={tags}";
		}
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				return restClient.post().uri(uri, parameters).body(contents).retrieve().body(String.class);
			} catch (Exception e) {
				logger.error("Retry attempt " + (retryCount + 1), e);
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while converting UML to Cucumber Guice file");
	}

	/**
	 * Convert UML model to Cucumber Spring file
	 *
	 * @param tags     Test case tags, can be blank or empty
	 * @param fileName The name of the file to convert
	 * @param contents The existing contents of the file (can be empty)
	 * @return The converted file content
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Convert UML to Cucumber Spring", description = "Convert a UML model element to a Spring-based Cucumber file")
	public String runConvertUMLToCucumberSpring(String tags, String fileName, String contents) throws Exception {
		logger.info("Converting UML to Cucumber Spring file: " + fileName + " with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("fileName", fileName);
		String uri = "/cucumber/runConvertUMLToCucumberSpring?fileName={fileName}";
		if (!tags.isEmpty()) {
			parameters.put("tags", tags);
			uri += "&tags={tags}";
		}
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				return restClient.post().uri(uri, parameters).body(contents).retrieve().body(String.class);
			} catch (Exception e) {
				logger.error("Retry attempt " + (retryCount + 1), e);
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while converting UML to Cucumber Spring file");
	}

	/**
	 * Get list of object names for UML to Cucumber conversion
	 *
	 * @param tags Test case tags, can be blank or empty
	 * @return JSON array of TransformableFile objects with file names
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Get UML to Cucumber object names", description = "Get the list of file names that can be converted from UML to standard Cucumber")
	public String getConvertUMLToCucumberObjectNames(String tags) throws Exception {
		logger.info("Getting UML to Cucumber object names with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		String uri = "/cucumber/getConvertUMLToCucumberObjectNames";
		if (!tags.isEmpty()) {
			parameters.put("tags", tags);
			uri += "?tags={tags}";
		}
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				return restClient.get().uri(uri, parameters).retrieve().body(String.class);
			} catch (Exception e) {
				logger.error("Retry attempt " + (retryCount + 1), e);
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while getting UML to Cucumber object names");
	}

	/**
	 * Get list of object names for UML to Cucumber Guice conversion
	 *
	 * @param tags Test case tags, can be blank or empty
	 * @return JSON array of TransformableFile objects with file names
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Get UML to Cucumber Guice object names", description = "Get the list of file names that can be converted from UML to Guice-based Cucumber")
	public String getConvertUMLToCucumberGuiceObjectNames(String tags) throws Exception {
		logger.info("Getting UML to Cucumber Guice object names with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		String uri = "/cucumber/getConvertUMLToCucumberGuiceObjectNames";
		if (!tags.isEmpty()) {
			parameters.put("tags", tags);
			uri += "?tags={tags}";
		}
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				return restClient.get().uri(uri, parameters).retrieve().body(String.class);
			} catch (Exception e) {
				logger.error("Retry attempt " + (retryCount + 1), e);
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while getting UML to Cucumber Guice object names");
	}

	/**
	 * Get list of object names for UML to Cucumber Spring conversion
	 *
	 * @param tags Test case tags, can be blank or empty
	 * @return JSON array of TransformableFile objects with file names
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Get UML to Cucumber Spring object names", description = "Get the list of file names that can be converted from UML to Spring-based Cucumber")
	public String getConvertUMLToCucumberSpringObjectNames(String tags) throws Exception {
		logger.info("Getting UML to Cucumber Spring object names with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		String uri = "/cucumber/getConvertUMLToCucumberSpringObjectNames";
		if (!tags.isEmpty()) {
			parameters.put("tags", tags);
			uri += "?tags={tags}";
		}
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				return restClient.get().uri(uri, parameters).retrieve().body(String.class);
			} catch (Exception e) {
				logger.error("Retry attempt " + (retryCount + 1), e);
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while getting UML to Cucumber Spring object names");
	}

}
