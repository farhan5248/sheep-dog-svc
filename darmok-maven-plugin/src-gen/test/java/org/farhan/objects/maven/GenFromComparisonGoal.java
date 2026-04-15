package org.farhan.objects.maven;

import java.util.HashMap;

public interface GenFromComparisonGoal {

    public void setExecutedWith(HashMap<String, String> keyMap);

    public void setModelComparison(HashMap<String, String> keyMap);

    public void setModelGreen(HashMap<String, String> keyMap);

    public void setModelRefactor(HashMap<String, String> keyMap);
}
