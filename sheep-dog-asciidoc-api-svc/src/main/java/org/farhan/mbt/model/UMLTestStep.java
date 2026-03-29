package org.farhan.mbt.model;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLTestStep {
    @JsonProperty("name")
    private String name;

    @JsonProperty("nameLong")
    private String nameLong;

    @JsonProperty("stepText")
    private String stepText;

    @JsonProperty("stepTable")
    private ArrayList<ArrayList<String>> stepTable;

    // Default constructor required for Jackson deserialization
    public UMLTestStep() {
        this.name = null;
        this.nameLong = null;
        // TODO these should be empty lists, not null
        this.stepText = null;
        this.stepTable = null;
    }

    public UMLTestStep(org.farhan.mbt.core.UMLTestStep umlTestStep) {
        name = umlTestStep.getName();
        nameLong = umlTestStep.getNameLong();
        stepText = umlTestStep.getStepText();
        stepTable = umlTestStep.getStepData();
    }

    public String getName() {
        return name;
    }

    public String getNameLong() {
        return nameLong;
    }

    public ArrayList<ArrayList<String>> getStepTable() {
        return stepTable;
    }

    public String getStepText() {
        return stepText;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameLong(String nameLong) {
        this.nameLong = nameLong;
    }

    public void setStepTable(ArrayList<ArrayList<String>> stepTable) {
        this.stepTable = stepTable;
    }

    public void setStepText(String stepText) {
        this.stepText = stepText;
    }

}
