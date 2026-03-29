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
import org.springframework.web.client.RestClientException;

@Service
public class AsciiDoctorService {

	Logger logger = LoggerFactory.getLogger(AsciiDoctorService.class);
	private static String BASE_URL;
	private final int RETRY_COUNT = 10;
	private final RestClient restClient;

	@Autowired
	public AsciiDoctorService(@Value("${sheepdog.host:sheep-dog-dev-svc}") String host,
			@Value("${sheepdog.port:80}") int port) {
		// TODO look into why using @ConfigurationProperties creates issues.
		// TODO why does this service need a controller?

		BASE_URL = "http://" + host + ":" + port;
		this.restClient = RestClient.builder()
				.baseUrl(BASE_URL)
				.build();
	}

	/**
	 * Clear objects for AsciiDoctor files
	 *
	 * @param tags Test case tags, can be blank or empty
	 * @throws RestClientException if the request fails
	 * @throws Exception           if the maximum number of retries is reached
	 */
	@Tool(name = "Clear objects for AsciiDoctor files", description = "Clear objects for AsciiDoctor files in the UML model")
	public void clearConvertAsciidoctorToUMLObjects(String tags) throws Exception {
		logger.info("Clearing objects for AsciiDoctor files with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		String uri = "/asciidoctor/clearConvertAsciidoctorToUMLObjects";
		if (!tags.isEmpty()) {
			parameters.put("tags", tags);
			uri += "?tags={tags}";
		}
		int retryCount = 0;
		while (retryCount < RETRY_COUNT) {
			try {
				restClient.delete().uri(uri, parameters).retrieve().body(Void.class);
				return; // Exit if successful
			} catch (Exception e) {
				logger.error("Retry attempt " + (retryCount + 1), e);
				Thread.sleep(1000);
				retryCount++;
			}
		}
		throw new Exception("Max retries reached while clearing objects for AsciiDoctor files");
	}

	/**
	 * Convert AsciiDoctor file to UML model
	 *
	 * @param tags     Test case tags, can be blank or empty
	 * @param fileName The name of the file to convert
	 * @param contents The contents of the file to convert
	 * @return The converted file content
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Convert AsciiDoctor to UML", description = "Convert an AsciiDoctor file to the UML model")
	public String runConvertAsciidoctorToUML(String tags, String fileName, String contents) throws Exception {
		logger.info("Converting AsciiDoctor file to UML: " + fileName + " with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("fileName", fileName);
		String uri = "/asciidoctor/runConvertAsciidoctorToUML?fileName={fileName}";
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
		throw new Exception("Max retries reached while converting AsciiDoctor file to UML");
	}

	/**
	 * Convert UML model to AsciiDoctor file
	 *
	 * @param tags     Test case tags, can be blank or empty
	 * @param fileName The name of the file to convert
	 * @param contents The existing contents of the file (can be empty)
	 * @return The converted file content
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Convert UML to AsciiDoctor", description = "Convert a UML model element to an AsciiDoctor file")
	public String runConvertUMLToAsciidoctor(String tags, String fileName, String contents) throws Exception {
		logger.info("Converting UML to AsciiDoctor file: " + fileName + " with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		parameters.put("fileName", fileName);
		String uri = "/asciidoctor/runConvertUMLToAsciidoctor?fileName={fileName}";
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
		throw new Exception("Max retries reached while converting UML to AsciiDoctor file");
	}

	/**
	 * Get list of object names for UML to AsciiDoctor conversion
	 *
	 * @param tags Test case tags, can be blank or empty
	 * @return JSON array of TransformableFile objects with file names
	 * @throws Exception if the maximum number of retries is reached
	 */
	@Tool(name = "Get UML to AsciiDoctor object names", description = "Get the list of file names that can be converted from UML to AsciiDoctor")
	public String getConvertUMLToAsciidoctorObjectNames(String tags) throws Exception {
		logger.info("Getting UML to AsciiDoctor object names with tags: " + tags);
		TreeMap<String, String> parameters = new TreeMap<String, String>();
		String uri = "/asciidoctor/getConvertUMLToAsciidoctorObjectNames";
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
		throw new Exception("Max retries reached while getting UML to AsciiDoctor object names");
	}

}