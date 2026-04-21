package org.farhan.objects.codeprj;

import java.util.HashMap;

public interface MetricsCsvFile {

    public String getAbsent(HashMap<String, String> keyMap);

    public String getAsFollows(HashMap<String, String> keyMap);

    public String getTimestamp(HashMap<String, String> keyMap);

    public String getGitBranch(HashMap<String, String> keyMap);

    public String getCommit(HashMap<String, String> keyMap);

    public String getScenario(HashMap<String, String> keyMap);

    public String getPhaseRedMs(HashMap<String, String> keyMap);

    public String getPhaseGreenMs(HashMap<String, String> keyMap);

    public String getPhaseRefactorMs(HashMap<String, String> keyMap);

    public String getPhaseTotalMs(HashMap<String, String> keyMap);
}
