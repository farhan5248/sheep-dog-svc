package org.farhan.objects.specprj.src.test.resources.asciidoc.stepdefs.blahapplication;

import java.util.HashMap;

public interface ObjectPageAsciidocFile {

    public void setCreatedAsFollows(HashMap<String, String> keyMap);

    public void setContent(HashMap<String, String> keyMap);

    public String getCreatedAsFollows(HashMap<String, String> keyMap);

    public String getContent(HashMap<String, String> keyMap);
}
