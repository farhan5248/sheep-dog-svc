package org.farhan.mbt;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpServerApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider asciidoctorTools(AsciiDoctorService asciidoctorService) {
		return MethodToolCallbackProvider.builder().toolObjects(asciidoctorService).build();
	}

	@Bean
	public ToolCallbackProvider cucumberTools(CucumberService cucumberService) {
		return MethodToolCallbackProvider.builder().toolObjects(cucumberService).build();
	}

}