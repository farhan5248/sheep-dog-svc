package org.farhan.mbt.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.farhan.dsl.grammar.IResourceRepository;
import org.farhan.mbt.model.TransformableFile;
import org.farhan.mbt.service.CucumberService;
import org.farhan.mbt.service.UMLService;
import org.farhan.mbt.service.cucumber.ConvertCucumberToUML;
import org.farhan.mbt.service.cucumber.ConvertUMLToCucumber;
import org.farhan.mbt.service.cucumber.ConvertUMLToCucumberGuice;
import org.farhan.mbt.service.cucumber.ConvertUMLToCucumberSpring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@ConfigurationProperties(prefix = "sheepdog")
@RestController
@RequestMapping("/cucumber")
public class CucumberController implements ApplicationListener<ApplicationReadyEvent> {

	Logger logger = LoggerFactory.getLogger(CucumberController.class);
	// TODO this repo and any mojo should be in the service layer, not the
	// controller
	private final IResourceRepository repository;
	private final CucumberService service;
	private final UMLService umlService;

	@Value("${sheepdog.host:sheep-dog-asciidoc-api-svc}")
	private String serverHost;

	@Value("${sheepdog.port:80}")
	private int serverPort;

	@Autowired
	public CucumberController(IResourceRepository repository, CucumberService service, UMLService umlService) {
		this.repository = repository;
		this.service = service;
		this.umlService = umlService;
	}

	@DeleteMapping("/clearConvertCucumberToUMLObjects")
	public void clearConvertCucumberToUMLObjects(
			@RequestParam(value = "tags", defaultValue = "") String tags) {
		logger.info("Starting clearConvertCucumberToUMLObjects");
		logger.info("tags:" + tags);
		service.clearObjects(new ConvertCucumberToUML(tags, repository));
		umlService.invalidateCache();
		logger.info("Ending clearConvertCucumberToUMLObjects");
	}

	@GetMapping("/getConvertCucumberToUMLFileChecksums")
	public List<TransformableFile> getConvertCucumberToUMLFileChecksums(
			@RequestParam(value = "tags", defaultValue = "") String tags) {
		logger.info("Starting getConvertCucumberToUMLFileChecksums");
		List<TransformableFile> fileList = service.getFileChecksums(repository, tags);
		logger.info("Ending getConvertCucumberToUMLFileChecksums");
		return fileList;
	}

	@DeleteMapping("/deleteConvertCucumberToUMLObject")
	public void deleteConvertCucumberToUMLObject(
			@RequestParam(value = "tags", defaultValue = "") String tags,
			@RequestParam(value = "fileName") String fileName) {
		logger.info("Starting deleteConvertCucumberToUMLObject");
		logger.info("tags:" + tags);
		logger.info("fileName:" + fileName);
		service.deleteObject(repository, tags, fileName);
		logger.info("Ending deleteConvertCucumberToUMLObject");
	}

	@GetMapping("/getConvertUMLToCucumberGuiceObjectNames")
	public List<TransformableFile> getConvertUMLToCucumberGuiceObjectNames(
			@RequestParam(value = "tags", defaultValue = "") String tags) {
		logger.info("Starting getConvertUMLToCucumberGuiceObjectNames");
		List<TransformableFile> fileList = service.getObjectNames(
				new ConvertUMLToCucumberGuice(tags, repository, serverHost, serverPort),
				tags);
		logger.info("Ending getConvertUMLToCucumberGuiceObjectNames");
		return fileList;
	}

	@GetMapping("/getConvertUMLToCucumberObjectNames")
	public List<TransformableFile> getConvertUMLToCucumberObjectNames(
			@RequestParam(value = "tags", defaultValue = "") String tags) {
		logger.info("Starting getConvertUMLToCucumberObjectNames");
		List<TransformableFile> fileList = service.getObjectNames(
				new ConvertUMLToCucumber(tags, repository, serverHost, serverPort), tags);
		logger.info("Ending getConvertUMLToCucumberObjectNames");
		return fileList;
	}

	@GetMapping("/getConvertUMLToCucumberSpringObjectNames")
	public List<TransformableFile> getConvertUMLToCucumberSpringObjectNames(
			@RequestParam(value = "tags", defaultValue = "") String tags) {
		logger.info("Starting getConvertUMLToCucumberSpringObjectNames");
		List<TransformableFile> fileList = service.getObjectNames(
				new ConvertUMLToCucumberSpring(tags, repository, serverHost, serverPort),
				tags);
		logger.info("Ending getConvertUMLToCucumberSpringObjectNames");
		return fileList;
	}

	@PostMapping("/runConvertCucumberToUML")
	public TransformableFile runConvertCucumberToUML(
			@RequestParam(value = "tags", defaultValue = "") String tags,
			@RequestParam(value = "fileName") String fileName, @RequestBody String contents) {
		logger.info("Starting runConvertCucumberToUML");
		logger.info("tags:" + tags);
		logger.info("fileName:" + fileName);
		TransformableFile mtr = new TransformableFile(fileName, contents, tags);
		service.convertSourceObject(
				new ConvertCucumberToUML(tags, repository),
				mtr);
		storeChecksum(tags, fileName, contents);
		logger.info("Ending runConvertCucumberToUML");
		return mtr;
	}

	private void storeChecksum(String tags, String fileName, String contents) {
		try {
			repository.put(tags, "checksums/" + fileName, computeSHA1(contents));
		} catch (Exception e) {
			logger.warn("Failed to store checksum for file: {}", fileName, e);
		}
	}

	private String computeSHA1(String content) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] hash = digest.digest(
					(content == null ? "" : content).getBytes(StandardCharsets.UTF_8));
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

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
	}

	@PostMapping("/runConvertUMLToCucumber")
	public TransformableFile runConvertUMLToCucumber(
			@RequestParam(value = "tags", defaultValue = "") String tags,
			@RequestParam(value = "fileName") String fileName, @RequestBody(required = false) String contents) {
		logger.info("Starting runConvertUMLToCucumber");
		logger.info("tags:" + tags);
		logger.info("fileName:" + fileName);
		TransformableFile mtr = service.convertObject(
				new ConvertUMLToCucumber(tags, repository, serverHost, serverPort),
				fileName, contents);
		logger.debug("response: " + mtr.toString());
		logger.info("Ending runConvertUMLToCucumber");
		return mtr;
	}

	@PostMapping("/runConvertUMLToCucumberGuice")
	public TransformableFile runConvertUMLToCucumberGuice(
			@RequestParam(value = "tags", defaultValue = "") String tags,
			@RequestParam(value = "fileName") String fileName, @RequestBody(required = false) String contents) {
		logger.info("Starting runConvertUMLToCucumberGuice");
		logger.info("tags:" + tags);
		logger.info("fileName:" + fileName);
		TransformableFile mtr = service.convertObject(
				new ConvertUMLToCucumberGuice(tags, repository, serverHost, serverPort),
				fileName, contents);
		logger.debug("response: " + mtr.toString());
		logger.info("Ending runConvertUMLToCucumberGuice");
		return mtr;
	}

	@PostMapping("/runConvertUMLToCucumberSpring")
	public TransformableFile runConvertUMLToCucumberSpring(
			@RequestParam(value = "tags", defaultValue = "") String tags,
			@RequestParam(value = "fileName") String fileName, @RequestBody(required = false) String contents) {
		logger.info("Starting runConvertUMLToCucumberSpring");
		logger.info("tags:" + tags);
		logger.info("fileName:" + fileName);
		TransformableFile mtr = service.convertObject(
				new ConvertUMLToCucumberSpring(tags, repository, serverHost, serverPort),
				fileName, contents);
		logger.debug("response: " + mtr.toString());
		logger.info("Ending runConvertUMLToCucumberSpring");
		return mtr;
	}

}