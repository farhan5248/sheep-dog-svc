package org.farhan.mbt.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.farhan.mbt.core.UMLTestStep;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLTestSetup {

    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    ArrayList<String> description;
    @JsonProperty("_links")
    HashMap<String, Object> _links;
    @JsonProperty("testStepList")
    ArrayList<String> testStepList;

    public UMLTestSetup() {
        this.name = null;
        this.description = null;
        this._links = new HashMap<String, Object>();
        this.testStepList = new ArrayList<String>();
    }

    public UMLTestSetup(org.farhan.mbt.core.UMLTestSetup umlTestSetup) {
        this.name = umlTestSetup.getName();
        String tempDescription = umlTestSetup.getDescription();
        if (tempDescription.isEmpty()) {
            this.description = new ArrayList<String>();
        } else {
            this.description = new ArrayList<>(Arrays.asList(tempDescription.split("\n")));
        }
        this._links = new HashMap<String, Object>();

        testStepList = new ArrayList<String>();
        _links.put("testStepList", testStepList);
        for (UMLTestStep umlTestStep : umlTestSetup.getTestStepList()) {
            testStepList.add(
                    "project/" + umlTestSetup.getParent().getParent().getId()
                            + "/suite/" + umlTestSetup.getParent().getId() + "/setup/step/" + umlTestStep.getId());
        }
    }

    public HashMap<String, Object> get_links() {
        return _links;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getTestStepList() {
        return testStepList;
    }

    public void set_links(HashMap<String, Object> _links) {
        this._links = _links;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTestStepList(ArrayList<String> testStepList) {
        this.testStepList = testStepList;
    }
}
