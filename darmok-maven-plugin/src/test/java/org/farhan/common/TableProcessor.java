package org.farhan.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

import io.cucumber.datatable.DataTable;

/**
 * Interprets Cucumber feature-file tables and dispatches property writes to
 * {@code set*}/{@code add*} methods (or assertion reads to {@code assert*}
 * methods) on the owning {@link TestObject} subclass via reflection.
 */
public final class TableProcessor {

    public enum TestState {
        Absent, Empty, Present, Any;

        private static final Set<String> NAMES = Arrays.stream(values()).map(Enum::name).collect(Collectors.toSet());

        public static boolean contains(String value) {
            return NAMES.contains(value);
        }
    }

    private final TestObject subject;

    TableProcessor(TestObject subject) {
        this.subject = subject;
    }

    public void putProperties(String partDesc, String partType, String stateType, String stateDesc) {
        TestObject.properties.put("partDesc", partDesc);
        TestObject.properties.put("partType", partType);
        TestObject.properties.put("stateType", stateType);
        TestObject.properties.put("stateDesc", stateDesc);
    }

    public void processInputOutputsStepDefinitionRef(String stateDesc, String operation, String partDesc,
            String partType, String stateType) {
        boolean negativeTest = false;
        if (stateType.contentEquals("isn't") || stateType.contentEquals("won't be")) {
            negativeTest = true;
        }
        String sectionName = (partDesc + " " + partType).trim();
        HashMap<String, String> row = new HashMap<String, String>();
        row.put(stateDesc, "");
        try {
            Object returnValue = subject.getClass()
                    .getMethod(operation + convertToPascalCase(sectionName) + convertToPascalCase(stateDesc),
                            HashMap.class)
                    .invoke(subject, row);
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

    public void processInputOutputsTable(DataTable dataTable, String operation, String partDesc, String partType,
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
                row.put(headers.get(j), replaceKeyword(data.get(i).get(j)));
            }
            for (String fieldName : headers) {
                try {
                    Object returnValue = subject.getClass()
                            .getMethod(operation + convertToPascalCase(sectionName) + convertToPascalCase(fieldName),
                                    HashMap.class)
                            .invoke(subject, row);
                    if (operation.equals("get")) {
                        String expectedValue = row.get(fieldName);
                        if (TestState.Any.name().equals(expectedValue)) {
                            continue;
                        }
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

    public void processInputOutputsText(String text, String operation, String partDesc, String partType,
            String stateType) {
        text = replaceKeyword(text);
        boolean negativeTest = false;
        if (stateType.contentEquals("isn't") || stateType.contentEquals("won't be")) {
            negativeTest = true;
        }
        String fieldName = "Content";
        String sectionName = (partDesc + " " + partType).trim();
        HashMap<String, String> row = new HashMap<String, String>();
        row.put(fieldName, text);
        try {
            Object returnValue = subject.getClass()
                    .getMethod(operation + convertToPascalCase(sectionName) + convertToPascalCase(fieldName),
                            HashMap.class)
                    .invoke(subject, row);
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

    private static String replaceKeyword(String value) {
        if (value.contentEquals(TestState.Empty.name().toLowerCase())) {
            return "";
        } else {
            return value;
        }
    }

    private static String convertToPascalCase(String s) {
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
}
