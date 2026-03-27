package org.farhan.dsl.asciidoc.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.resource.SaveOptions;
import org.farhan.dsl.grammar.IDescription;
import org.farhan.dsl.grammar.ITestStepContainer;
import org.farhan.dsl.grammar.ITestSuite;
import org.farhan.dsl.asciidoc.asciiDoc.TestStepContainer;
import org.farhan.dsl.asciidoc.asciiDoc.TestSuite;

public class TestSuiteImpl implements ITestSuite {

    TestSuite eObject;
    private String fullName;
    private Object container;

    public TestSuiteImpl(TestSuite testSuite) {
        this.eObject = testSuite;
    }

    @Override
    public Object getContainer() {
        if (container != null)
            return container;
        return org.farhan.dsl.grammar.SheepDogFactory.instance.createTestProject();
    }

    public String getContent() throws Exception {
        Resource theResource = new ResourceSetImpl().createResource(URI.createFileURI("tmpFile.asciidoc"));
        theResource.getContents().add(eObject);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        theResource.save(os, SaveOptions.newBuilder().format().getOptions().toOptionsMap());
        return os.toString();
    }

    @Override
    public IDescription getDescription() {
        if (eObject.getDescription() != null) {
            return new DescriptionImpl(eObject.getDescription());
        }
        return null;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getName() {
        return eObject.getName();
    }

    @Override
    public EList<ITestStepContainer> getTestStepContainerList() {
        EList<ITestStepContainer> list = new BasicEList<ITestStepContainer>() {
			@Override
			public boolean add(ITestStepContainer element) {
				eObject.getTestStepContainerList().add(((TestStepContainerImpl) element).eObject);
				return super.add(element);
			}
		};
        for (TestStepContainer tsc : eObject.getTestStepContainerList()) {
            TestStepContainerImpl tsci = new TestStepContainerImpl(tsc);
            list.add(tsci);
        }
        return list;
    }

    @Override
    public void setContainer(Object value) {
        this.container = value;
    }

    public void setContent(String text) throws Exception {
        if (!text.isEmpty()) {
            Resource theResource = new ResourceSetImpl().createResource(URI.createFileURI("tmpFile.asciidoc"));
            theResource.load(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), Collections.EMPTY_MAP);
            eObject = (TestSuite) theResource.getContents().get(0);
        }
    }

    @Override
    public void setDescription(IDescription value) {
        eObject.setDescription(((DescriptionImpl) value).eObject);
    }

    @Override
    public void setFullName(String value) {
        this.fullName = value;
        String extension = org.farhan.dsl.grammar.SheepDogFactory.instance.createTestProject().getFileExtension();
        eObject.setName((new File(fullName)).getName().replaceFirst(extension + "$", ""));
    }

    @Override
    public void setName(String value) {
        eObject.setName(value);
    }

}
