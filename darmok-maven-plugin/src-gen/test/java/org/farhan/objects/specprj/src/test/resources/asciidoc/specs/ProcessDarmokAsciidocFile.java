package org.farhan.objects.specprj.src.test.resources.asciidoc.specs;

import java.util.HashMap;

public interface ProcessDarmokAsciidocFile {

    public void setCreatedAsFollows(HashMap<String, String> keyMap);

    public void setContent(HashMap<String, String> keyMap);

    public String getAsFollows(HashMap<String, String> keyMap);

    public String getState(HashMap<String, String> keyMap);

    public String getCreatedAsFollows(HashMap<String, String> keyMap);

    public String getContent(HashMap<String, String> keyMap);
}
