package org.farhan.objects.codeprj;

import java.util.HashMap;

public interface DarmokRunnersLogFile {

    public String getAsFollows(HashMap<String, String> keyMap);

    public String getLevel(HashMap<String, String> keyMap);

    public String getCategory(HashMap<String, String> keyMap);

    public String getContent(HashMap<String, String> keyMap);

    public String getAsFollowsWithThisFailure(HashMap<String, String> keyMap);

    public String getEmpty(HashMap<String, String> keyMap);
}
