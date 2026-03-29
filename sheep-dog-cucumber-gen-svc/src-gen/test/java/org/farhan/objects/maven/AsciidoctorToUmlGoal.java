package org.farhan.objects.maven;

import java.util.HashMap;

public interface AsciidoctorToUmlGoal {

    public void setExecuted(HashMap<String, String> keyMap);

    public void setExecutedWith(HashMap<String, String> keyMap);

    public void setTags(HashMap<String, String> keyMap);
}
