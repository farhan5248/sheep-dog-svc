package org.farhan.mbt.model;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLTestData {
    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    ArrayList<String> description;
    @JsonProperty("tags")
    ArrayList<String> tags;
    @JsonProperty("dataTable")
    private ArrayList<ArrayList<String>> dataTable;

    public UMLTestData() {
        this.name = null;
        this.description = null;
        this.tags = null;
        this.dataTable = new ArrayList<ArrayList<String>>();
    }

    public UMLTestData(org.farhan.mbt.core.UMLTestData umlTestData) {
        this.name = umlTestData.getName();
        String tempDescription = umlTestData.getDescription();
        if (tempDescription.isEmpty()) {
            this.description = new ArrayList<String>();
        } else {
            this.description = new ArrayList<>(Arrays.asList(tempDescription.split("\n")));
        }
        this.tags = umlTestData.getTags();
        this.dataTable = umlTestData.getRowList();
    }

    public ArrayList<ArrayList<String>> getDataTable() {
        return dataTable;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setDataTable(ArrayList<ArrayList<String>> dataTable) {
        this.dataTable = dataTable;
    }

    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

}
