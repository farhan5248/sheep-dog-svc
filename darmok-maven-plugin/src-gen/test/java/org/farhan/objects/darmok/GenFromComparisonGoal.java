package org.farhan.objects.darmok;

import java.util.HashMap;

public interface GenFromComparisonGoal {

    public void setExecutedAndSucceedsWith(HashMap<String, String> keyMap);

    public void setModelComparison(HashMap<String, String> keyMap);

    public void setModelGreen(HashMap<String, String> keyMap);

    public void setModelRefactor(HashMap<String, String> keyMap);

    public void setGreenFullPathsEnabled(HashMap<String, String> keyMap);
}
