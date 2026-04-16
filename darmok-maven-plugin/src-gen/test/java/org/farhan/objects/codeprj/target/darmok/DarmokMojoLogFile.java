package org.farhan.objects.codeprj.target.darmok;

import java.util.HashMap;

public interface DarmokMojoLogFile {

    public String getAsFollows(HashMap<String, String> keyMap);

    public String getLevel(HashMap<String, String> keyMap);

    public String getCategory(HashMap<String, String> keyMap);

    public String getContent(HashMap<String, String> keyMap);

    public String getState(HashMap<String, String> keyMap);
}
