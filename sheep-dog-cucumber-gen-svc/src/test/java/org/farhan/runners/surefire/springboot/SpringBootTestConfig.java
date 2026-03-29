package org.farhan.runners.surefire.springboot;

import org.farhan.mbt.RestServiceApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@ComponentScan(basePackages = { "org.farhan.mbt", "org.farhan.common", "org.farhan.impl" })
@EnableAutoConfiguration
@ActiveProfiles("surefire")
@SpringBootTest(classes = RestServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class SpringBootTestConfig {
}
