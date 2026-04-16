package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import org.farhan.objects.codeprj.ScenariosListTxtFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Impl for {@code code-prj/scenarios-list.txt}. Inherits file-level state
 * assertions from {@link AbstractFileImpl}.
 */
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class ScenariosListTxtFileImpl extends AbstractFileImpl implements ScenariosListTxtFile {
}
