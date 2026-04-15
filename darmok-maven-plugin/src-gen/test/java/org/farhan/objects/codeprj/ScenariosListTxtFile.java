package org.farhan.objects.codeprj;

import java.util.HashMap;

public interface ScenariosListTxtFile {

    public void setCreatedAsFollows(HashMap<String, String> keyMap);

    public void setContent(HashMap<String, String> keyMap);

    public String getAsFollows(HashMap<String, String> keyMap);
}
