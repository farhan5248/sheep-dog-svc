package org.farhan.dsl.asciidoc.impl;

import org.farhan.dsl.grammar.ICell;
import org.farhan.dsl.grammar.IDescription;
import org.farhan.dsl.grammar.ILine;
import org.farhan.dsl.grammar.IResourceRepository;
import org.farhan.dsl.grammar.IRow;
import org.farhan.dsl.grammar.IStepDefinition;
import org.farhan.dsl.grammar.IStepObject;
import org.farhan.dsl.grammar.IStepParameters;
import org.farhan.dsl.grammar.ITable;
import org.farhan.dsl.grammar.ITestCase;
import org.farhan.dsl.grammar.ITestProject;
import org.farhan.dsl.grammar.ITestSetup;
import org.farhan.dsl.grammar.ITestStep;
import org.farhan.dsl.grammar.ITestSuite;
import org.farhan.dsl.grammar.IText;
import org.farhan.dsl.asciidoc.asciiDoc.AsciiDocFactory;

public class AsciiDocFactoryImpl extends org.farhan.dsl.grammar.impl.SheepDogFactoryImpl {

	private TestProjectImpl testProject;
	private IResourceRepository sr;

	public AsciiDocFactoryImpl(IResourceRepository sourceFileRepository) {
		sr = sourceFileRepository;
	}

	@Override
	public IDescription createDescription() {
		return new DescriptionImpl(AsciiDocFactory.eINSTANCE.createDescription());
	}

	@Override
	public IStepDefinition createStepDefinition() {
		return new StepDefinitionImpl(AsciiDocFactory.eINSTANCE.createStepDefinition());
	}

	@Override
	public IStepObject createStepObject() {
		return new StepObjectImpl(AsciiDocFactory.eINSTANCE.createStepObject());
	}

	@Override
	public IStepParameters createStepParameters() {
		return new StepParametersImpl(AsciiDocFactory.eINSTANCE.createStepParameters());
	}

	@Override
	public ITestCase createTestCase() {
		return new TestCaseImpl(AsciiDocFactory.eINSTANCE.createTestCase());
	}

	@Override
	public ITestSetup createTestSetup() {
		return new TestSetupImpl(AsciiDocFactory.eINSTANCE.createTestSetup());
	}

	@Override
	public ITestStep createTestStep() {
		return new TestStepImpl(AsciiDocFactory.eINSTANCE.createTestStep());
	}

	@Override
	public ITestSuite createTestSuite() {
		return new TestSuiteImpl(AsciiDocFactory.eINSTANCE.createTestSuite());
	}

	@Override
	public ITestProject createTestProject() {
		if (testProject == null) {
			testProject = new TestProjectImpl(sr);
		}
		return testProject;
	}

	@Override
	public ILine createLine() {
		return new LineImpl(AsciiDocFactory.eINSTANCE.createLine());
	}

	@Override
	public ITable createTable() {
		return new TableImpl(AsciiDocFactory.eINSTANCE.createTable());
	}

	@Override
	public IRow createRow() {
		return new RowImpl(AsciiDocFactory.eINSTANCE.createRow());
	}

	@Override
	public ICell createCell() {
		return new CellImpl(AsciiDocFactory.eINSTANCE.createCell());
	}

	@Override
	public IText createText() {
		return new TextImpl(AsciiDocFactory.eINSTANCE.createText());
	}

}
