package org.farhan.objects.maven;

import java.util.HashMap;

public interface GenFromExistingGoal {

    public void setExecuted(HashMap<String, String> keyMap);

    public void setExecutedWith(HashMap<String, String> keyMap);

    public void setStage(HashMap<String, String> keyMap);

    public void setModelGreen(HashMap<String, String> keyMap);

    public void setModelRefactor(HashMap<String, String> keyMap);
}
