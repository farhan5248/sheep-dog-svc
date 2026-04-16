package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import org.farhan.objects.codeprj.src.main.java.org.farhan.objects.LogoutHappyPathJavaFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class LogoutHappyPathJavaFileImpl extends AbstractFileImpl implements LogoutHappyPathJavaFile {
}
