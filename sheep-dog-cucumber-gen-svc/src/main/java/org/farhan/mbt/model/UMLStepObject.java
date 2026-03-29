package org.farhan.mbt.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.farhan.mbt.core.UMLStepDefinition;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UMLStepObject {

    @JsonProperty("name")
    private String name;
    @JsonProperty("qualifiedName")
    String qualifiedName;
    @JsonProperty("_links")
    HashMap<String, Object> _links;
    @JsonProperty("stepDefinitionList")
    ArrayList<String> stepDefinitionList;

    public UMLStepObject() {
        this.name = null;
        this.qualifiedName = null;
        this._links = new HashMap<String, Object>();
        this.stepDefinitionList = new ArrayList<String>();
    }

    public UMLStepObject(org.farhan.mbt.core.UMLStepObject umlStepObject) {
        name = umlStepObject.getName();
        this.qualifiedName = umlStepObject.getUmlElement().getQualifiedName();
        this._links = new HashMap<String, Object>();

        stepDefinitionList = new ArrayList<String>();
        _links.put("stepDefinitionList", stepDefinitionList);
        for (UMLStepDefinition umlStepDefinition : umlStepObject.getStepDefinitionList()) {
            stepDefinitionList.add(
                    "project/" + umlStepDefinition.getParent().getParent().getId()
                            + "/object/" + umlStepDefinition.getParent().getId() + "/definition/"
                            + umlStepDefinition.getId());
        }
    }

    public String getName() {
        return name;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public ArrayList<String> getStepDefinitionList() {
        return stepDefinitionList;
    }

    public void setName(String nameLong) {
        this.name = nameLong;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public void setStepDefinitionList(ArrayList<String> testStepList) {
        this.stepDefinitionList = testStepList;
    }
}
