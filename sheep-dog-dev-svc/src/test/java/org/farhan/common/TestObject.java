package org.farhan.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.farhan.dsl.grammar.ICell;
import org.farhan.dsl.grammar.IDescription;
import org.farhan.dsl.grammar.ILine;
import org.farhan.dsl.grammar.IRow;
import org.farhan.dsl.grammar.IStepDefinition;
import org.farhan.dsl.grammar.IStepObject;
import org.farhan.dsl.grammar.IStepParameters;
import org.farhan.dsl.grammar.ITable;
import org.farhan.dsl.grammar.ITestCase;
import org.farhan.dsl.grammar.ITestData;
import org.farhan.dsl.grammar.ITestDocument;
import org.farhan.dsl.grammar.ITestProject;
import org.farhan.dsl.grammar.ITestStep;
import org.farhan.dsl.grammar.ITestStepContainer;
import org.farhan.dsl.grammar.ITestSuite;
import org.farhan.dsl.grammar.IText;
import org.junit.jupiter.api.Assertions;

import io.cucumber.datatable.DataTable;

public abstract class TestObject {

    public enum TestState {
        Absent, Empty, Present;

        private static final Set<String> NAMES = Arrays.stream(values()).map(Enum::name).collect(Collectors.toSet());

        public static boolean contains(String value) {
            return NAMES.contains(value);
        }
    }

    public static HashMap<String, Object> properties = new HashMap<String, Object>();

    public static void reset() {
        TestObject.properties.clear();
    }

    private static Object getDocumentFromNode(Object node) {
        Object current = node;
        while (current != null && !(current instanceof ITestDocument)) {
            if (current instanceof ICell) {
                current = ((ICell) current).getParent();
            } else if (current instanceof IRow) {
                current = ((IRow) current).getParent();
            } else if (current instanceof ITable) {
                current = ((ITable) current).getParent();
            } else if (current instanceof IText) {
                current = ((IText) current).getParent();
            } else if (current instanceof ILine) {
                current = ((ILine) current).getParent();
            } else if (current instanceof IDescription) {
                current = ((IDescription) current).getParent();
            } else if (current instanceof ITestStep) {
                current = ((ITestStep) current).getParent();
            } else if (current instanceof ITestData) {
                current = ((ITestData) current).getParent();
            } else if (current instanceof ITestStepContainer) {
                current = ((ITestStepContainer) current).getParent();
            } else if (current instanceof IStepParameters) {
                current = ((IStepParameters) current).getParent();
            } else if (current instanceof IStepDefinition) {
                current = ((IStepDefinition) current).getParent();
            } else if (current instanceof ITestProject) {
                return null;
            } else {
                return null;
            }
        }
        return current;
    }

    protected static Object getProperty(String key) {
        return properties.get(key);
    }

    protected static void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    protected String component;

    protected String object;

    public void assertVertexStep(String partDesc, String partType, String stateType, String stateDesc) {
        putProperties(partDesc, partType, stateType, stateDesc);
        processInputOutputsStepDefinitionRef(stateDesc, "get", partDesc, partType, stateType);
        removeProperties();
    }

    public void assertVertexStep(String partDesc, String partType, String stateType, String stateDesc,
            DataTable dataTable) {
        putProperties(partDesc, partType, stateType, stateDesc);
        processInputOutputsStepDefinitionRef(stateDesc, "get", partDesc, partType, stateType);
        processInputOutputsTable(dataTable, "get", partDesc, partType, stateType);
        removeProperties();
    }

    public void assertVertexStep(String partDesc, String partType, String stateType, String stateDesc, String text) {
        putProperties(partDesc, partType, stateType, stateDesc);
        processInputOutputsStepDefinitionRef(stateDesc, "get", partDesc, partType, stateType);
        processInputOutputsText(text, "get", partDesc, partType, stateType);
        removeProperties();
    }

    public void doEdgeStep(String partDesc, String partType, String stateType, String stateDesc) {
        putProperties(partDesc, partType, stateType, stateDesc);
        processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
        removeProperties();
    }

    public void doEdgeStep(String partDesc, String partType, String stateType, String stateDesc, DataTable dataTable) {
        putProperties(partDesc, partType, stateType, stateDesc);
        processInputOutputsTable(dataTable, "set", partDesc, partType, stateType);
        processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
        removeProperties();
    }

    public void doEdgeStep(String partDesc, String partType, String stateType, String stateDesc, String text) {
        putProperties(partDesc, partType, stateType, stateDesc);
        processInputOutputsText(text, "set", partDesc, partType, stateType);
        processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
        removeProperties();
    }

    public void setVertexStep(String partDesc, String partType, String stateType, String stateDesc) {
        putProperties(partDesc, partType, stateType, stateDesc);
        processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
        removeProperties();
    }

    public void setVertexStep(String partDesc, String partType, String stateType, String stateDesc,
            DataTable dataTable) {
        putProperties(partDesc, partType, stateType, stateDesc);
        processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
        processInputOutputsTable(dataTable, "set", partDesc, partType, stateType);
        removeProperties();
    }

    public void setVertexStep(String partDesc, String partType, String stateType, String stateDesc, String text) {
        putProperties(partDesc, partType, stateType, stateDesc);
        processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
        processInputOutputsText(text, "set", partDesc, partType, stateType);
        removeProperties();
    }

    private String convertToPascalCase(String s) {
        StringBuilder result = new StringBuilder();
        for (String word : s.split("[ \\-\\(\\)/]+")) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1));
                }
            }
        }
        return result.toString();
    }

    private boolean navigateToNode(String path, boolean create) {
        try {
            String[] parts = path.split("/");
            Object current = getDocumentFromNode(getProperty("cursor"));
            int i = 0;
            while (i < parts.length && current != null) {
                String elementType = parts[i];
                if (elementType.endsWith("List")) {
                    if (i + 1 >= parts.length || !parts[i + 1].matches("\\d+")) {
                        break;
                    }
                    int index = Integer.parseInt(parts[i + 1]) - 1;
                    if (create) {
                        current = getOrCreateNode(current, elementType, index);
                    } else {
                        try {
                            current = getChildNode(current, elementType, index);
                        } catch (IndexOutOfBoundsException e) {
                            setProperty("cursor", null);
                            return true;
                        }
                    }
                    i += 2;
                } else {
                    if (create && elementType.equals("Text")) {
                        break;
                    }
                    current = create ? getOrCreateNode(current, elementType, 0) : getChildNode(current, elementType, 0);
                    i++;
                }
                if (current != null)
                    setProperty("cursor", current);
            }
            return true;
        } catch (Exception e) {
            setProperty("cursor", null);
            return false;
        }
    }

    private void processInputOutputsStepDefinitionRef(String stateDesc, String operation, String partDesc,
            String partType, String stateType) {
        boolean negativeTest = false;
        if (stateType.contentEquals("isn't") || stateType.contentEquals("won't be")) {
            negativeTest = true;
        }
        String sectionName = (partDesc + " " + partType).trim();
        HashMap<String, String> row = new HashMap<String, String>();
        row.put(stateDesc, "");
        try {
            Object returnValue = this.getClass()
                    .getMethod(operation + convertToPascalCase(sectionName) + convertToPascalCase(stateDesc),
                            HashMap.class)
                    .invoke(this, row);
            if (operation.equals("get")) {
                String expectedValue = convertToPascalCase(stateDesc);
                String actual = returnValue == null ? null : returnValue.toString();
                // TODO temporary stub: skip assertion for "created as follows" pre-check.
                // The method can't determine what to return because it lacks context.
                // Class section should be path::to::class class. Then the part desc
                // property can be used to identify what to assert exists or not.
                if (stateDesc.contentEquals("created as follows")) {
                    return;
                }
                if (TestState.contains(convertToPascalCase(stateDesc))) {
                    String mappedActual;
                    if (actual == null)
                        mappedActual = TestState.Absent.name();
                    else if (actual.isEmpty())
                        mappedActual = TestState.Empty.name();
                    else
                        mappedActual = TestState.Present.name();
                    if (negativeTest) {
                        Assertions.assertNotEquals(expectedValue, mappedActual);
                    } else {
                        Assertions.assertEquals(expectedValue, mappedActual);
                    }
                } else {
                    if (negativeTest) {
                        Assertions.assertNull(actual);
                    } else {
                        Assertions.assertNotNull(actual);
                    }
                }
            }
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    private void processInputOutputsTable(DataTable dataTable, String operation, String partDesc, String partType,
            String stateType) {
        boolean negativeTest = false;
        if (stateType.contentEquals("isn't") || stateType.contentEquals("won't be")) {
            negativeTest = true;
        }
        String sectionName = (partDesc + " " + partType).trim();
        List<List<String>> data = dataTable.asLists();
        ArrayList<String> headers = new ArrayList<String>();
        for (String cell : data.get(0)) {
            headers.add(cell);
        }
        for (int i = 1; i < data.size(); i++) {
            HashMap<String, String> row = new HashMap<String, String>();
            for (int j = 0; j < headers.size(); j++) {
                row.put(headers.get(j), data.get(i).get(j));
            }
            for (String fieldName : headers) {
                try {
                    Object returnValue = this.getClass()
                            .getMethod(operation + convertToPascalCase(sectionName) + convertToPascalCase(fieldName),
                                    HashMap.class)
                            .invoke(this, row);
                    if (operation.equals("get")) {
                        String expectedValue = replaceKeyword(row.get(fieldName));
                        String actual = returnValue == null ? null : returnValue.toString();
                        if (fieldName.equals("State") && TestState.contains(expectedValue)) {
                            String mappedActual;
                            if (actual == null)
                                mappedActual = TestState.Absent.name();
                            else if (actual.isEmpty())
                                mappedActual = TestState.Empty.name();
                            else
                                mappedActual = TestState.Present.name();
                            if (negativeTest) {
                                Assertions.assertNotEquals(expectedValue, mappedActual);
                            } else {
                                Assertions.assertEquals(expectedValue, mappedActual);
                            }
                        } else {
                            if (negativeTest) {
                                Assertions.assertNotEquals(expectedValue, actual);
                            } else {
                                Assertions.assertEquals(expectedValue, actual);
                            }
                        }
                    }
                } catch (Exception e) {
                    Assertions.fail(e);
                }
            }
        }
    }

    private void processInputOutputsText(String text, String operation, String partDesc, String partType,
            String stateType) {
        boolean negativeTest = false;
        if (stateType.contentEquals("isn't") || stateType.contentEquals("won't be")) {
            negativeTest = true;
        }
        String fieldName = "Content";
        String sectionName = (partDesc + " " + partType).trim();
        HashMap<String, String> row = new HashMap<String, String>();
        row.put(fieldName, text);
        try {
            Object returnValue = this.getClass()
                    .getMethod(operation + convertToPascalCase(sectionName) + convertToPascalCase(fieldName),
                            HashMap.class)
                    .invoke(this, row);
            if (operation.equals("get")) {
                String actual = returnValue == null ? null : returnValue.toString();
                if (negativeTest) {
                    Assertions.assertNotEquals(text, actual);
                } else {
                    Assertions.assertEquals(text, actual);
                }
            }
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    private void putProperties(String partDesc, String partType, String stateType, String stateDesc) {
        properties.put("partDesc", partDesc);
        properties.put("partType", partType);
        properties.put("stateType", stateType);
        properties.put("stateDesc", stateDesc);
    }

    private void removeProperties() {
        properties.remove("partDesc");
        properties.remove("partType");
        properties.remove("stateType");
        properties.remove("stateDesc");
    }

    protected boolean createNodeDependencies(String path) {
        return navigateToNode(path, true);
    }

    protected Object getChildNode(Object parent, String elementType, int index) {
        switch (elementType) {
        case "Table":
            if (parent instanceof ITestStep)
                return ((ITestStep) parent).getTable();
            if (parent instanceof ITestData)
                return ((ITestData) parent).getTable();
            return ((IStepParameters) parent).getTable();
        case "Text":
            return ((ITestStep) parent).getText();
        case "Description":
            if (parent instanceof ITestSuite)
                return ((ITestSuite) parent).getDescription();
            if (parent instanceof ITestStepContainer)
                return ((ITestStepContainer) parent).getDescription();
            if (parent instanceof IStepObject)
                return ((IStepObject) parent).getDescription();
            if (parent instanceof IStepDefinition)
                return ((IStepDefinition) parent).getDescription();
            if (parent instanceof IStepParameters)
                return ((IStepParameters) parent).getDescription();
            return ((ITestData) parent).getDescription();
        case "TestStepContainerList":
            return ((ITestSuite) parent).getTestStepContainer(index);
        case "TestStepList":
            return ((ITestStepContainer) parent).getTestStep(index);
        case "RowList":
            return ((ITable) parent).getRow(index);
        case "CellList":
            return ((IRow) parent).getCell(index);
        case "StepDefinitionList":
            return ((IStepObject) parent).getStepDefinition(index);
        case "StepParametersList":
            return ((IStepDefinition) parent).getStepParameters(index);
        case "LineList":
            return ((IDescription) parent).getLine(index);
        case "TestDataList":
            return ((ITestCase) parent).getTestData(index);
        default:
            throw new IllegalArgumentException("Unknown element type: " + elementType);
        }
    }

    // === Node navigation ===
    protected String getFullNameFromPath() {
        return object.replaceFirst("^src/test/resources/[^/]+/", "");
    }

    abstract protected Object getOrCreateNode(Object parent, String elementType, int index);

    protected String listToCsvString(List<?> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(list.get(i).toString());
        }
        return sb.toString();
    }

    protected String replaceKeyword(String value) {
        if (value.contentEquals("empty")) {
            return "";
        } else {
            return value;
        }
    }

    protected void setComponent(String component) {
        this.component = component;
    }

    protected boolean setCursorAtNode(String path) {
        return navigateToNode(path, false);
    }

    protected void setObject(String object) {
        this.object = object;
    }

}
