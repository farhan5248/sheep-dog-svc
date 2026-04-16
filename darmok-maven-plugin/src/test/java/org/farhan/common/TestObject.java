package org.farhan.common;

import java.util.HashMap;
import java.util.List;

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
import org.farhan.dsl.grammar.ITestProject;
import org.farhan.dsl.grammar.ITestStep;
import org.farhan.dsl.grammar.ITestStepContainer;
import org.farhan.dsl.grammar.ITestSuite;
import org.farhan.dsl.grammar.ISheepDogFactory;
import org.farhan.dsl.grammar.SheepDogBuilder;
import org.farhan.dsl.grammar.SheepDogUtility;
import org.farhan.dsl.grammar.SheepDogFactory;
import org.junit.jupiter.api.Assertions;

import io.cucumber.datatable.DataTable;

/**
 * Common base for project-specific test scaffolding
 * ({@code TestObject{Language}Impl}).
 *
 * <p>
 * Purpose: abstract the traversal and mutation of the DSL model when running
 * tests, and provide a uniform way to interpret Cucumber feature-file tables.
 * Shared across all projects that use the sheep-dog grammar.
 *
 * <p>
 * The {@code add*}/{@code assert*}/{@code set*} helpers (defined in
 * project-specific subclasses) support two feature-file table styles:
 * <ul>
 * <li>Vertical — one value per row, single data column. Each row produces a
 * sibling child under the same parent. After creating a child the helpers
 * navigate the cursor back up to the parent so the next row's {@code add*}
 * call targets the parent correctly.</li>
 * <li>Horizontal — one row with many columns. Each column fills a different
 * property (e.g. Step Definition, Step Parameter, Cell Name) within the same
 * model element; no cursor navigation between columns.</li>
 * </ul>
 *
 * <p>
 * This table-style interpretation is a test-framework concern, distinct from
 * LSP service simulation. Classes that simulate LSP behavior (issue proposal
 * application, test-step name parsing, etc.) live in the
 * {@code org.farhan.mock} package.
 *
 * <p>
 * Cursor model: a single {@code cursor} property tracks the currently focused
 * model element. {@link #navigateToDocument()} positions it at a document
 * boundary; {@link #navigateToNode(String, boolean)} walks a path within a
 * document.
 */
public abstract class TestObject {

    public static HashMap<String, Object> properties = new HashMap<String, Object>();

    private final PathNavigator pathNavigator = new PathNavigator();

    private final TableProcessor tableProcessor = new TableProcessor(this);

    public PathNavigator getPathNavigator() {
        return pathNavigator;
    }

    public TableProcessor getTableProcessor() {
        return tableProcessor;
    }

    public static void reset() {
        TestObject.properties.clear();
        SheepDogFactory.instance = ISheepDogFactory.eINSTANCE;
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
        tableProcessor.putProperties(partDesc, partType, stateType, stateDesc);
        tableProcessor.processInputOutputsStepDefinitionRef(stateDesc, "get", partDesc, partType, stateType);
    }

    public void assertVertexStep(String partDesc, String partType, String stateType, String stateDesc,
            DataTable dataTable) {
        tableProcessor.putProperties(partDesc, partType, stateType, stateDesc);
        tableProcessor.processInputOutputsStepDefinitionRef(stateDesc, "get", partDesc, partType, stateType);
        tableProcessor.processInputOutputsTable(dataTable, "get", partDesc, partType, stateType);
    }

    public void assertVertexStep(String partDesc, String partType, String stateType, String stateDesc, String text) {
        tableProcessor.putProperties(partDesc, partType, stateType, stateDesc);
        tableProcessor.processInputOutputsStepDefinitionRef(stateDesc, "get", partDesc, partType, stateType);
        tableProcessor.processInputOutputsText(text, "get", partDesc, partType, stateType);
    }

    public void doEdgeStep(String partDesc, String partType, String stateType, String stateDesc) {
        tableProcessor.putProperties(partDesc, partType, stateType, stateDesc);
        tableProcessor.processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
    }

    public void doEdgeStep(String partDesc, String partType, String stateType, String stateDesc, DataTable dataTable) {
        tableProcessor.putProperties(partDesc, partType, stateType, stateDesc);
        tableProcessor.processInputOutputsTable(dataTable, "set", partDesc, partType, stateType);
        tableProcessor.processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
    }

    public void doEdgeStep(String partDesc, String partType, String stateType, String stateDesc, String text) {
        tableProcessor.putProperties(partDesc, partType, stateType, stateDesc);
        tableProcessor.processInputOutputsText(text, "set", partDesc, partType, stateType);
        tableProcessor.processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
    }

    public void setVertexStep(String partDesc, String partType, String stateType, String stateDesc) {
        tableProcessor.putProperties(partDesc, partType, stateType, stateDesc);
        tableProcessor.processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
    }

    public void setVertexStep(String partDesc, String partType, String stateType, String stateDesc,
            DataTable dataTable) {
        tableProcessor.putProperties(partDesc, partType, stateType, stateDesc);
        tableProcessor.processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
        tableProcessor.processInputOutputsTable(dataTable, "set", partDesc, partType, stateType);
    }

    public void setVertexStep(String partDesc, String partType, String stateType, String stateDesc, String text) {
        tableProcessor.putProperties(partDesc, partType, stateType, stateDesc);
        tableProcessor.processInputOutputsStepDefinitionRef(stateDesc, "set", partDesc, partType, stateType);
        tableProcessor.processInputOutputsText(text, "set", partDesc, partType, stateType);
    }

    protected String getFullNameFromPath() {
        return object.replaceFirst("^src/test/resources/[^/]+/", "");
    }

    // === add* helpers ===

    protected void addCellWithName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ICell) {
            cursor = SheepDogUtility.getRowParent(cursor);
        }
        setProperty("cursor", SheepDogBuilder.createCell((IRow) cursor, name));
    }

    protected void addLineWithContent(String content) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ILine) {
            cursor = SheepDogUtility.getDescriptionParent(cursor);
        }
        setProperty("cursor", SheepDogBuilder.createLine((IDescription) cursor, content));
    }

    protected void addRowWithContent(String content) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IRow) {
            cursor = SheepDogUtility.getTableParent(cursor);
        }
        IRow row = SheepDogBuilder.createRow((ITable) cursor);
        SheepDogBuilder.createCell(row, content);
        setProperty("cursor", row);
    }

    protected void addStepDefinitionWithName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IStepDefinition) {
            cursor = SheepDogUtility.getStepObjectParent(cursor);
        }
        setProperty("cursor", SheepDogBuilder.createStepDefinition((IStepObject) cursor, name));
    }

    protected void addStepObjectWithFullName(String stepObjectName) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IStepObject) {
            cursor = SheepDogUtility.getTestProjectParent(cursor);
        }
        setProperty("cursor",
                SheepDogBuilder.createStepObject((ITestProject) getProperty("workspace"), stepObjectName));
    }

    protected void addStepParametersWithName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IStepParameters) {
            cursor = SheepDogUtility.getStepDefinitionParent(cursor);
        }
        setProperty("cursor", SheepDogBuilder.createStepParameters((IStepDefinition) cursor, name));
    }

    protected void addTable() {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestStep) {
            setProperty("cursor", SheepDogBuilder.createTable((ITestStep) cursor));
        } else if (cursor instanceof IStepParameters) {
            setProperty("cursor", SheepDogBuilder.createTable((IStepParameters) cursor));
        }
    }

    protected void addTestCaseWithName(String testStepContainerName) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestCase) {
            cursor = SheepDogUtility.getTestSuiteParent(cursor);
        }
        setProperty("cursor", SheepDogBuilder.createTestCase((ITestSuite) cursor, testStepContainerName));
    }

    protected void addTestDataWithName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestData) {
            cursor = SheepDogUtility.getTestCaseParent(cursor);
        }
        setProperty("cursor", SheepDogBuilder.createTestData((ITestCase) cursor, name));
    }

    protected void addTestSetupWithName(String testSetupName) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestStepContainer) {
            cursor = SheepDogUtility.getTestSuiteParent(cursor);
        }
        setProperty("cursor", SheepDogBuilder.createTestSetup((ITestSuite) cursor, testSetupName));
    }

    protected void addTestSuiteWithFullName(String testSuiteFullName) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestSuite) {
            cursor = SheepDogUtility.getTestProjectParent(cursor);
        }
        setProperty("cursor",
                SheepDogBuilder.createTestSuite((ITestProject) getProperty("workspace"), testSuiteFullName));
    }

    protected void addTextWithContent(String content) {
        setProperty("cursor", SheepDogBuilder.createText((ITestStep) getProperty("cursor"), content));
    }

    // === cursor query helpers ===

    protected IDescription getDescriptionFromCursor() {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestSuite)
            return ((ITestSuite) cursor).getDescription();
        else if (cursor instanceof ITestStepContainer)
            return ((ITestStepContainer) cursor).getDescription();
        else if (cursor instanceof IStepObject)
            return ((IStepObject) cursor).getDescription();
        else if (cursor instanceof IStepDefinition)
            return ((IStepDefinition) cursor).getDescription();
        else if (cursor instanceof IStepParameters)
            return ((IStepParameters) cursor).getDescription();
        else if (cursor instanceof ITestData)
            return ((ITestData) cursor).getDescription();
        return null;
    }

    protected ITable getTableFromCursor() {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IStepParameters)
            return ((IStepParameters) cursor).getTable();
        else if (cursor instanceof ITestData)
            return ((ITestData) cursor).getTable();
        else if (cursor instanceof ITestStep)
            return ((ITestStep) cursor).getTable();
        return null;
    }

    // === assert* helpers ===

    protected String assertCellName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ICell) {
            return ((ICell) cursor).getName();
        } else {
            Object cell = SheepDogUtility.getCell((IRow) cursor, name);
            setProperty("cursor", cell);
            return cell == null ? null : ((ICell) cell).getName();
        }
    }

    protected String assertLineContent(String content) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ILine) {
            return ((ILine) cursor).getContent();
        } else {
            Object line = SheepDogUtility.getLine((IDescription) cursor, content);
            setProperty("cursor", line);
            return line == null ? null : ((ILine) line).getContent();
        }
    }

    protected String assertRowContent(String content) {
        Object cell = SheepDogUtility.getCell((IRow) getProperty("cursor"), content);
        setProperty("cursor", cell);
        return cell == null ? null : ((ICell) cell).getName();
    }

    protected String assertStepDefinitionName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IStepDefinition) {
            return ((IStepDefinition) cursor).getName();
        } else {
            Object sd = SheepDogUtility.getStepDefinition((IStepObject) cursor, name);
            setProperty("cursor", sd);
            return sd == null ? null : ((IStepDefinition) sd).getName();
        }
    }

    protected String assertStepObjectName(String name) {
        return ((IStepObject) getProperty("cursor")).getName();
    }

    protected String assertStepParametersName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IStepParameters) {
            return ((IStepParameters) cursor).getName();
        } else {
            Object sp = SheepDogUtility.getStepParameters((IStepDefinition) cursor, name);
            setProperty("cursor", sp);
            return sp == null ? null : ((IStepParameters) sp).getName();
        }
    }

    protected String assertTestDataName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestData) {
            return ((ITestData) cursor).getName();
        } else {
            Object td = SheepDogUtility.getTestData((ITestCase) cursor, name);
            setProperty("cursor", td);
            return td == null ? null : ((ITestData) td).getName();
        }
    }

    protected String assertTestStepContainerName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestStepContainer) {
            return ((ITestStepContainer) cursor).getName();
        } else {
            Object tsc = SheepDogUtility.getTestStepContainer((ITestSuite) cursor, name);
            setProperty("cursor", tsc);
            return tsc == null ? null : ((ITestStepContainer) tsc).getName();
        }
    }

    protected String assertTestStepFullName(String fullName) {
        return ((ITestStep) getProperty("cursor")).getFullName();
    }

    protected String assertStepDefinitionRefName(String name) {
        return ((ITestStep) getProperty("cursor")).getStepDefinitionName();
    }

    protected String assertStepObjectRefName(String name) {
        return ((ITestStep) getProperty("cursor")).getStepObjectName();
    }

    protected String assertTestSuiteName(String name) {
        return ((ITestSuite) getProperty("cursor")).getName();
    }

    // === set* helpers ===

    protected void setStepDefinitionName(String name) {
        ((ITestStep) getProperty("cursor")).setStepDefinitionName(name);
    }

    protected void setTestSuiteName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestSuite) {
            ((ITestSuite) cursor).setName(name);
        }
    }

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

    protected void setComponent(String component) {
        this.component = component;
    }

    protected void setObject(String object) {
        this.object = object;
    }

}
