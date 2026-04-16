package org.farhan.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.farhan.dsl.grammar.IDescription;
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
import org.farhan.dsl.grammar.SheepDogBuilder;
import org.farhan.dsl.grammar.SheepDogUtility;

/**
 * Sheep-dog-grammar path/cursor helpers. Reads and writes {@code cursor} and
 * {@code workspace} on {@link TestObject#properties}.
 */
public final class PathNavigator {

    PathNavigator() {
    }

    public boolean createNodeDependencies(String path) {
        try {
            EObject document = (EObject) SheepDogUtility
                    .getTestDocumentParent((EObject) TestObject.getProperty("cursor"));
            Resource resource = document.eResource();
            StringBuilder fragment = new StringBuilder(resource.getURIFragment(document));
            Object current = document;
            for (PathSegment seg : parsePath(path)) {
                appendSegment(fragment, seg);
                Object found = resource.getEObject(fragment.toString());
                if (found != null) {
                    current = found;
                } else {
                    current = createNode(current, seg.type());
                    if (current == null)
                        break;
                }
                TestObject.setProperty("cursor", current);
            }
            return true;
        } catch (Exception e) {
            System.err.println("PathNavigator.createNodeDependencies failed for path=" + path + ": " + e);
            TestObject.setProperty("cursor", null);
            return false;
        }
    }

    public void navigateToDocument(String fullName) {
        TestObject.setProperty("cursor", SheepDogUtility.getTestDocument(
                (ITestProject) TestObject.getProperty("workspace"), fullName));
    }

    public void navigateToDocument() {
        if (TestObject.getProperty("Test Suite Full Name") != null) {
            navigateToDocument(TestObject.getProperty("Test Suite Full Name").toString());
        } else if (TestObject.getProperty("Step Object Full Name") != null) {
            navigateToDocument(TestObject.getProperty("Step Object Full Name").toString());
        }
    }

    public boolean navigateToNode(String path, boolean fallback) {
        try {
            EObject document = (EObject) SheepDogUtility
                    .getTestDocumentParent((EObject) TestObject.getProperty("cursor"));
            if (document == null) {
                TestObject.setProperty("cursor", null);
                return true;
            }
            Resource resource = document.eResource();
            List<PathSegment> reversed = parsePath(path, true);
            // An invalid/empty path (e.g. "stepDefinitionList" missing its index)
            // parses to an empty segment list and resolves to the document itself.
            if (reversed.isEmpty()) {
                TestObject.setProperty("cursor", document);
                return true;
            }
            for (int drop = 0; drop < reversed.size(); drop++) {
                StringBuilder fragment = new StringBuilder(resource.getURIFragment(document));
                for (int i = reversed.size() - 1; i >= drop; i--) {
                    appendSegment(fragment, reversed.get(i));
                }
                EObject target = resource.getEObject(fragment.toString());
                if (target != null) {
                    TestObject.setProperty("cursor", target);
                    return true;
                }
                if (!fallback) {
                    TestObject.setProperty("cursor", null);
                    return true;
                }
            }
            TestObject.setProperty("cursor", null);
            return true;
        } catch (Exception e) {
            System.err.println("PathNavigator.navigateToNode failed for path=" + path + ": " + e);
            TestObject.setProperty("cursor", null);
            return true;
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
