package org.farhan.objects.codeprj.srcgen.test.resources.cucumber.specs.app;

import java.util.HashMap;

public interface ProcessFeatureFile {

    public void setCreatedAsFollows(HashMap<String, String> keyMap);

    public void setContent(HashMap<String, String> keyMap);

    public String getCreatedAsFollows(HashMap<String, String> keyMap);

    public String getContent(HashMap<String, String> keyMap);
}
