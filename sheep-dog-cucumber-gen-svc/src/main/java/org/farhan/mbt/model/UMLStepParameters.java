package org.farhan.mbt.model;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLStepParameters {

    @JsonProperty("stepTable")
    private ArrayList<ArrayList<String>> stepParametersTable;

    // Default constructor required for Jackson deserialization
    public UMLStepParameters() {
        this.stepParametersTable = null;
    }

    public UMLStepParameters(org.farhan.mbt.core.UMLStepParameters umlStepParameters) {
        stepParametersTable = umlStepParameters.getRowList();
    }

    public ArrayList<ArrayList<String>> getStepParametersTable() {
        return stepParametersTable;
    }

    public void setStepParametersTable(ArrayList<ArrayList<String>> stepParametersTable) {
        this.stepParametersTable = stepParametersTable;
    }
}
