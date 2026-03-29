package org.farhan.mbt.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.farhan.mbt.core.UMLStepParameters;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UMLStepDefinition {

    @JsonProperty("nameLong")
    private String nameLong;
    @JsonProperty("_links")
    HashMap<String, Object> _links;
    @JsonProperty("stepParametersList")
    ArrayList<String> stepParametersList;

    public UMLStepDefinition() {
        this.nameLong = null;
        this._links = new HashMap<String, Object>();
        this.stepParametersList = new ArrayList<String>();
    }

    public UMLStepDefinition(org.farhan.mbt.core.UMLStepDefinition umlStepDefinition) {
        nameLong = umlStepDefinition.getNameLong();
        this._links = new HashMap<String, Object>();

        stepParametersList = new ArrayList<String>();
        _links.put("stepParametersList", stepParametersList);
        for (UMLStepParameters umlStepParameters : umlStepDefinition.getStepParametersList()) {
            stepParametersList.add(
                    "project/" + umlStepDefinition.getParent().getParent().getId()
                            + "/object/" + umlStepDefinition.getParent().getId() + "/definition/" + umlStepDefinition.getId()
                            + "/parameters/" + umlStepParameters.getId());
        }
    }

    public String getNameLong() {
        return nameLong;
    }

    public ArrayList<String> getStepParametersList() {
        return stepParametersList;
    }

    public void setNameLong(String nameLong) {
        this.nameLong = nameLong;
    }

    public void setStepParametersList(ArrayList<String> testStepList) {
        this.stepParametersList = testStepList;
    }
}
