package org.farhan.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.farhan.dsl.grammar.SheepDogBuilder;
import org.farhan.dsl.grammar.SheepDogUtility;
import org.farhan.mock.TestStepNameParser;

/**
 * {@link TestObject} subclass that adds cursor/path navigation helpers for
 * projects that manipulate the sheep-dog-grammar DSL model. The
 * {@code cursor} property on {@link TestObject#properties} tracks the
 * currently focused model element; {@link #navigateToDocument()} positions it
 * at a document boundary and {@link #navigateToNode(String, boolean)} walks a
 * path within a document.
 */
public abstract class PathNavigatorTestObject extends TestObject {

    private static final Logger logger = LoggerFactory.getLogger(PathNavigatorTestObject.class);

    public boolean createNodeDependencies(String path) {
        logger.debug("createNodeDependencies path={}", path);
        try {
            EObject document = (EObject) SheepDogUtility
                    .getAncestor(getProperty("cursor"), ITestDocument.class);
            Resource resource = document.eResource();
            StringBuilder fragment = new StringBuilder(resource.getURIFragment(document));
            Object current = document;
            for (PathSegment seg : parsePath(path)) {
                appendSegment(fragment, seg);
                Object found = resource.getEObject(fragment.toString());
                if (found != null) {
                    logger.debug("createNodeDependencies resolved segment={} fragment={}", seg.type(), fragment);
                    current = found;
                } else {
                    logger.debug("createNodeDependencies creating segment={} fragment={}", seg.type(), fragment);
                    current = createNode(current, seg.type());
                    if (current == null) {
                        logger.debug("createNodeDependencies createNode returned null segment={}", seg.type());
                        break;
                    }
                }
                setProperty("cursor", current);
            }
            return true;
        } catch (Exception e) {
            logger.error("createNodeDependencies failed for path={}", path, e);
            setProperty("cursor", null);
            return false;
        }
    }

    public void navigateToDocument(String fullName) {
        ITestProject workspace = (ITestProject) getProperty("workspace");
        ITestDocument match = workspace.getTestDocumentList().stream()
                .filter(td -> td.getFullName().contentEquals(fullName))
                .findFirst().orElse(null);
        if (match == null && logger.isDebugEnabled()) {
            List<String> available = new ArrayList<>();
            for (ITestDocument td : workspace.getTestDocumentList()) {
                available.add(td.getFullName());
            }
            logger.debug("navigateToDocument no match fullName={} available={}", fullName, available);
        } else {
            logger.debug("navigateToDocument fullName={} matched={}", fullName, match != null);
        }
        setProperty("cursor", match);
    }

    public void navigateToDocument() {
        if (getProperty("Test Suite Full Name") != null) {
            navigateToDocument(getProperty("Test Suite Full Name").toString());
        } else if (getProperty("Step Object Full Name") != null) {
            navigateToDocument(getProperty("Step Object Full Name").toString());
        }
    }

    public boolean navigateToNode(String path, boolean fallback) {
        logger.debug("navigateToNode path={} fallback={}", path, fallback);
        try {
            EObject document = (EObject) SheepDogUtility
                    .getAncestor(getProperty("cursor"), ITestDocument.class);
            if (document == null) {
                logger.debug("navigateToNode no document ancestor for cursor; clearing cursor");
                setProperty("cursor", null);
                return true;
            }
            Resource resource = document.eResource();
            List<PathSegment> reversed = parsePath(path, true);
            // An invalid/empty path (e.g. "stepDefinitionList" missing its index)
            // parses to an empty segment list and resolves to the document itself.
            if (reversed.isEmpty()) {
                logger.debug("navigateToNode empty segments; cursor=document");
                setProperty("cursor", document);
                return true;
            }
            for (int drop = 0; drop < reversed.size(); drop++) {
                StringBuilder fragment = new StringBuilder(resource.getURIFragment(document));
                for (int i = reversed.size() - 1; i >= drop; i--) {
                    appendSegment(fragment, reversed.get(i));
                }
                EObject target = resource.getEObject(fragment.toString());
                if (target != null) {
                    logger.debug("navigateToNode resolved drop={} fragment={}", drop, fragment);
                    setProperty("cursor", target);
                    return true;
                }
                if (!fallback) {
                    logger.debug("navigateToNode no resolve and fallback=false; clearing cursor fragment={}", fragment);
                    setProperty("cursor", null);
                    return true;
                }
            }
            logger.debug("navigateToNode exhausted fallback drops path={}", path);
            setProperty("cursor", null);
            return true;
        } catch (Exception e) {
            logger.error("navigateToNode failed for path={}", path, e);
            setProperty("cursor", null);
            return true;
        }
    }

    // === add* helpers ===

    protected void addCellWithName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ICell) {
            cursor = SheepDogUtility.getAncestor(cursor, IRow.class);
        }
        setProperty("cursor", SheepDogBuilder.createCell((IRow) cursor, name));
    }

    protected void addLineWithContent(String content) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ILine) {
            cursor = SheepDogUtility.getAncestor(cursor, IDescription.class);
        }
        setProperty("cursor", SheepDogBuilder.createLine((IDescription) cursor, content));
    }

    protected void addRowWithContent(String content) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IRow) {
            cursor = SheepDogUtility.getAncestor(cursor, ITable.class);
        }
        IRow row = SheepDogBuilder.createRow((ITable) cursor);
        SheepDogBuilder.createCell(row, content);
        setProperty("cursor", row);
    }

    protected void addStepDefinitionWithName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IStepDefinition) {
            cursor = SheepDogUtility.getAncestor(cursor, IStepObject.class);
        }
        setProperty("cursor", SheepDogBuilder.createStepDefinition((IStepObject) cursor, name));
    }

    protected void addStepObjectWithFullName(String stepObjectName) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IStepObject) {
            cursor = SheepDogUtility.getAncestor(cursor, ITestProject.class);
        }
        setProperty("cursor",
                SheepDogBuilder.createStepObject((ITestProject) getProperty("workspace"), stepObjectName));
    }

    protected void addStepParametersWithName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IStepParameters) {
            cursor = SheepDogUtility.getAncestor(cursor, IStepDefinition.class);
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
            cursor = SheepDogUtility.getAncestor(cursor, ITestSuite.class);
        }
        setProperty("cursor", SheepDogBuilder.createTestCase((ITestSuite) cursor, testStepContainerName));
    }

    protected void addTestDataWithName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestData) {
            cursor = SheepDogUtility.getAncestor(cursor, ITestCase.class);
        }
        setProperty("cursor", SheepDogBuilder.createTestData((ITestCase) cursor, name));
    }

    protected void addTestSetupWithName(String testSetupName) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestStepContainer) {
            cursor = SheepDogUtility.getAncestor(cursor, ITestSuite.class);
        }
        setProperty("cursor", SheepDogBuilder.createTestSetup((ITestSuite) cursor, testSetupName));
    }

    protected void addTestStepWithFullName(String stepName) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestStep) {
            cursor = SheepDogUtility.getAncestor(cursor, ITestStepContainer.class);
        }
        setProperty("cursor",
                SheepDogBuilder.createTestStep((ITestStepContainer) cursor,
                        TestStepNameParser.getStepObjectName(stepName),
                        TestStepNameParser.getStepDefinitionName(stepName)));
    }

    protected void addTestSuiteWithFullName(String testSuiteFullName) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestSuite) {
            cursor = SheepDogUtility.getAncestor(cursor, ITestProject.class);
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
            ICell cell = ((IRow) cursor).getCellList().stream()
                    .filter(c -> c.getName().contentEquals(name))
                    .findFirst().orElse(null);
            if (cell == null && logger.isDebugEnabled()) {
                List<String> available = new ArrayList<>();
                for (ICell c : ((IRow) cursor).getCellList()) {
                    available.add(c.getName());
                }
                logger.debug("assertCellName no match name={} available={}", name, available);
            }
            setProperty("cursor", cell);
            return cell == null ? null : cell.getName();
        }
    }

    protected String assertLineContent(String content) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ILine) {
            return ((ILine) cursor).getContent();
        } else {
            ILine line = ((IDescription) cursor).getLineList().stream()
                    .filter(l -> l.getContent().equals(content))
                    .findFirst().orElse(null);
            if (line == null && logger.isDebugEnabled()) {
                List<String> available = new ArrayList<>();
                for (ILine l : ((IDescription) cursor).getLineList()) {
                    available.add(l.getContent());
                }
                logger.debug("assertLineContent no match content={} available={}", content, available);
            }
            setProperty("cursor", line);
            return line == null ? null : line.getContent();
        }
    }

    protected String assertRowContent(String content) {
        ICell cell = ((IRow) getProperty("cursor")).getCellList().stream()
                .filter(c -> c.getName().contentEquals(content))
                .findFirst().orElse(null);
        setProperty("cursor", cell);
        return cell == null ? null : cell.getName();
    }

    protected String assertStepDefinitionName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof IStepDefinition) {
            return ((IStepDefinition) cursor).getName();
        } else {
            IStepDefinition sd = ((IStepObject) cursor).getStepDefinitionList().stream()
                    .filter(s -> s.getName().contentEquals(name))
                    .findFirst().orElse(null);
            if (sd == null && logger.isDebugEnabled()) {
                List<String> available = new ArrayList<>();
                for (IStepDefinition s : ((IStepObject) cursor).getStepDefinitionList()) {
                    available.add(s.getName());
                }
                logger.debug("assertStepDefinitionName no match name={} available={}", name, available);
            }
            setProperty("cursor", sd);
            return sd == null ? null : sd.getName();
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
            IStepParameters sp = ((IStepDefinition) cursor).getStepParametersList().stream()
                    .filter(p -> name.contentEquals(p.getName()))
                    .findFirst().orElse(null);
            if (sp == null && logger.isDebugEnabled()) {
                List<String> available = new ArrayList<>();
                for (IStepParameters p : ((IStepDefinition) cursor).getStepParametersList()) {
                    available.add(p.getName());
                }
                logger.debug("assertStepParametersName no match name={} available={}", name, available);
            }
            setProperty("cursor", sp);
            return sp == null ? null : sp.getName();
        }
    }

    protected String assertTestDataName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestData) {
            return ((ITestData) cursor).getName();
        } else {
            ITestData td = ((ITestCase) cursor).getTestDataList().stream()
                    .filter(d -> d.getName().contentEquals(name))
                    .findFirst().orElse(null);
            if (td == null && logger.isDebugEnabled()) {
                List<String> available = new ArrayList<>();
                for (ITestData d : ((ITestCase) cursor).getTestDataList()) {
                    available.add(d.getName());
                }
                logger.debug("assertTestDataName no match name={} available={}", name, available);
            }
            setProperty("cursor", td);
            return td == null ? null : td.getName();
        }
    }

    protected String assertTestStepContainerName(String name) {
        Object cursor = getProperty("cursor");
        if (cursor instanceof ITestStepContainer) {
            return ((ITestStepContainer) cursor).getName();
        } else {
            ITestStepContainer tsc = ((ITestSuite) cursor).getTestStepContainerList().stream()
                    .filter(c -> c.getName().contentEquals(name))
                    .findFirst().orElse(null);
            if (tsc == null && logger.isDebugEnabled()) {
                List<String> available = new ArrayList<>();
                for (ITestStepContainer c : ((ITestSuite) cursor).getTestStepContainerList()) {
                    available.add(c.getName());
                }
                logger.debug("assertTestStepContainerName no match name={} available={}", name, available);
            }
            setProperty("cursor", tsc);
            return tsc == null ? null : tsc.getName();
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

    /**
     * A single segment of a node path. {@code isList} distinguishes list
     * segments (which consume two slash-separated parts: name + 1-based index)
     * from scalar segments (single part, index defaults to 0).
     */
    private static record PathSegment(String type, int index, boolean isList) {
    }

    private static List<PathSegment> parsePath(String path) {
        return parsePath(path, false);
    }

    private static List<PathSegment> parsePath(String path, boolean reverse) {
        List<PathSegment> segments = new ArrayList<>();
        String[] parts = path.split("/");
        int i = 0;
        while (i < parts.length) {
            String type = parts[i];
            if (type.endsWith("List")) {
                if (i + 1 >= parts.length || !parts[i + 1].matches("\\d+")) {
                    break;
                }
                segments.add(new PathSegment(type, Integer.parseInt(parts[i + 1]) - 1, true));
                i += 2;
            } else {
                segments.add(new PathSegment(type, 0, false));
                i++;
            }
        }
        if (reverse) {
            Collections.reverse(segments);
        }
        return segments;
    }

    private static void appendSegment(StringBuilder fragment, PathSegment seg) {
        fragment.append("/@").append(seg.type());
        if (seg.isList()) {
            fragment.append(".").append(seg.index());
        }
    }

    private static Object createNode(Object parent, String elementType) {
        switch (elementType) {
        case "table":
            if (parent instanceof ITestStep)
                return SheepDogBuilder.createTable((ITestStep) parent);
            if (parent instanceof ITestData)
                return SheepDogBuilder.createTable((ITestData) parent);
            return SheepDogBuilder.createTable((IStepParameters) parent);
        case "description":
            if (parent instanceof ITestSuite)
                return SheepDogBuilder.createDescription((ITestSuite) parent);
            if (parent instanceof ITestStepContainer)
                return SheepDogBuilder.createDescription((ITestStepContainer) parent);
            if (parent instanceof IStepObject)
                return SheepDogBuilder.createDescription((IStepObject) parent);
            if (parent instanceof IStepDefinition)
                return SheepDogBuilder.createDescription((IStepDefinition) parent);
            if (parent instanceof IStepParameters)
                return SheepDogBuilder.createDescription((IStepParameters) parent);
            return SheepDogBuilder.createDescription((ITestData) parent);
        case "testStepContainerList":
            return SheepDogBuilder.createTestCase((ITestSuite) parent, "Test Case");
        case "testStepList":
            return SheepDogBuilder.createTestStep((ITestStepContainer) parent, "", "");
        case "rowList":
            return SheepDogBuilder.createRow((ITable) parent);
        case "cellList":
            return SheepDogBuilder.createCell((IRow) parent, "");
        case "stepDefinitionList":
            return SheepDogBuilder.createStepDefinition((IStepObject) parent, "");
        case "stepParametersList":
            return SheepDogBuilder.createStepParameters((IStepDefinition) parent, "");
        case "lineList":
            return SheepDogBuilder.createLine((IDescription) parent, "");
        case "testDataList":
            return SheepDogBuilder.createTestData((ITestCase) parent, "");
        default:
            return null;
        }
    }
}
