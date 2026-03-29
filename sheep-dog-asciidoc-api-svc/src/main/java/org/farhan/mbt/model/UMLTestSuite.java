package org.farhan.mbt.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.farhan.mbt.core.UMLTestCase;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLTestSuite {

    @JsonProperty("name")
    String name;
    // TODO rename NameLong elsewhere to qualifiedName
    @JsonProperty("qualifiedName")
    String qualifiedName;
    @JsonProperty("description")
    ArrayList<String> description;

    @JsonProperty("tags")
    ArrayList<String> tags;
    @JsonProperty("_links")
    HashMap<String, Object> _links;
    @JsonProperty("testSetup")
    String testSetup;
    @JsonProperty("testCaseList")
    ArrayList<String> testCaseList;

    public UMLTestSuite() {
        this.name = null;
        this.qualifiedName = null;
        this.description = null;
        this.tags = null;
        this._links = new HashMap<String, Object>();
        this.testSetup = null;
        this.testCaseList = new ArrayList<String>();
    }

    public UMLTestSuite(org.farhan.mbt.core.UMLTestSuite umlTestSuite) {
        this.name = umlTestSuite.getName();
        this.qualifiedName = umlTestSuite.getUmlElement().getQualifiedName();
        String tempDescription = umlTestSuite.getDescription();
        if (tempDescription.isEmpty()) {
            this.description = new ArrayList<String>();
        } else {
            this.description = new ArrayList<>(Arrays.asList(tempDescription.split("\n")));
        }
        this.tags = umlTestSuite.getTags();
        this._links = new HashMap<String, Object>();

        if (umlTestSuite.hasTestSetup()) {
            testSetup = "project/" + umlTestSuite.getParent().getId()
                    + "/suite/" + umlTestSuite.getId() + "/setup";
        } else {
            testSetup = null;
        }
        _links.put("testSetup", testSetup);

        testCaseList = new ArrayList<String>();
        _links.put("testCaseList", testCaseList);
        for (UMLTestCase umlTestCase : umlTestSuite.getTestCaseList()) {
            testCaseList.add(
                    "project/" + umlTestSuite.getParent().getId()
                            + "/suite/" + umlTestSuite.getId() + "/case/" + umlTestCase.getId());
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

    public String getQualifiedName() {
        return qualifiedName;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<String> getTestCaseList() {
        return testCaseList;
    }

    public String getTestSetup() {
        return testSetup;
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

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setTestCaseList(ArrayList<String> testCaseList) {
        this.testCaseList = testCaseList;
    }

    public void setTestSetup(String testSetup) {
        this.testSetup = testSetup;
    }

}
