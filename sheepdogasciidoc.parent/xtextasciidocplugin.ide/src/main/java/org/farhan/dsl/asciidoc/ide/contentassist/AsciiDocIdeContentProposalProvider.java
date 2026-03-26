package org.farhan.dsl.asciidoc.ide.contentassist;

import org.slf4j.Logger;
import org.farhan.dsl.grammar.SheepDogLoggerFactory;

import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistEntry;
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor;
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider;
import org.farhan.dsl.issues.*;
import org.farhan.dsl.grammar.SheepDogIssueProposal;
import org.farhan.dsl.grammar.ITestProject;
import org.farhan.dsl.grammar.SheepDogFactory;

import java.io.File;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.Assignment;
import org.farhan.dsl.asciidoc.asciiDoc.TestStep;
import org.farhan.dsl.asciidoc.impl.TestStepImpl;

/**
 * See
 * https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#content-assist
 * on how to customize the content assistant.
 */
public class AsciiDocIdeContentProposalProvider extends IdeContentProposalProvider {

	private static final Logger logger = SheepDogLoggerFactory.getLogger(AsciiDocIdeContentProposalProvider.class);

	@Override
	protected void _createProposals(Assignment assignment, ContentAssistContext context,
			IIdeContentProposalAcceptor acceptor) {
		logger.debug("Entering _createProposals with assignment: {}",
				assignment != null ? assignment.getFeature() : "null");
		try {
			if (context == null) {
				logger.warn("ContentAssistContext is null, cannot provide proposals");
				return;
			}

			if (context.getCurrentModel() != null && context.getCurrentModel() instanceof TestStep) {
				TestStep step = (TestStep) context.getCurrentModel();
				String feature = assignment != null ? assignment.getFeature() : "";

				if ("stepObjectName".equals(feature)) {
					completeStepObject(step, assignment, context, acceptor);
				} else if ("stepDefinitionName".equals(feature)) {
					completeStepDefinitionName(step, assignment, context, acceptor);
				} else if ("cellList".equals(feature)) {
					completeCellList(step, assignment, context, acceptor);
				}
			}

			super._createProposals(assignment, context, acceptor);
			logger.debug("Exiting _createProposals");
		} catch (Exception e) {
			logger.error("Error creating proposals for assignment '{}'",
					assignment != null ? assignment.getFeature() : "null", e);
		}
	}

	private void completeStepObject(TestStep step, Assignment assignment, ContentAssistContext context,
			IIdeContentProposalAcceptor acceptor) {
		TestStepImpl testStep = new TestStepImpl(step);
		logger.debug("Entering completeStepObject for element: {} {}", testStep.getStepObjectName(), testStep.getStepDefinitionName());
		try {
			initProject(step.eResource());
			for (SheepDogIssueProposal p : TestStepIssueResolver.suggestStepObjectNameWorkspace(testStep)) {
				ContentAssistEntry proposal = getProposalCreator().createSnippet(
						p.getValue().toString(), p.getId(), context);
				if (proposal != null) {
					proposal.setDocumentation(p.getDescription());
					acceptor.accept(proposal, 0);
				}
			}
		} catch (Exception e) {
			logger.error("Failed in content assist for {} {}", testStep.getStepObjectName(), testStep.getStepDefinitionName(), e);
		}
		logger.debug("Exiting completeStepObject");
	}

	private void completeStepDefinitionName(TestStep step, Assignment assignment, ContentAssistContext context,
			IIdeContentProposalAcceptor acceptor) {
		TestStepImpl testStep = new TestStepImpl(step);
		logger.debug("Entering completeStepDefinitionName for element: {} {}", testStep.getStepObjectName(), testStep.getStepDefinitionName());
		try {
			initProject(step.eResource());
			for (SheepDogIssueProposal p : TestStepIssueResolver.suggestStepDefinitionNameWorkspace(testStep)) {
				ContentAssistEntry proposal = getProposalCreator().createSnippet(
						p.getValue().toString(), p.getId(), context);
				if (proposal != null) {
					proposal.setDocumentation(p.getDescription());
					acceptor.accept(proposal, 0);
				}
			}
		} catch (Exception e) {
			logger.error("Failed in content assist for {} {}", testStep.getStepObjectName(), testStep.getStepDefinitionName(), e);
		}
		logger.debug("Exiting completeStepDefinitionName");
	}

	private void completeCellList(TestStep step, Assignment assignment, ContentAssistContext context,
			IIdeContentProposalAcceptor acceptor) {
		logger.debug("Entering completeCellList for element: {}", step.toString());
		try {
			initProject(step.eResource());
			for (SheepDogIssueProposal p : RowIssueResolver.suggestCellListWorkspace(new TestStepImpl(step))) {
				ContentAssistEntry proposal = getProposalCreator().createSnippet(
						p.getValue().toString(), p.getId(), context);
				if (proposal != null) {
					proposal.setDocumentation(p.getDescription());
					acceptor.accept(proposal, 0);
				}
			}
		} catch (Exception e) {
			logger.error("Failed in content assist for {}", step.toString(), e);
		}
		logger.debug("Exiting completeCellList");
	}

	private void initProject(Resource resource) {
		ITestProject parent = SheepDogFactory.instance.createTestProject();
		String resourcePath = resource.getURI().toFileString().replace(File.separator, "/");
		String projectPath = resourcePath.split("src/test/resources/asciidoc/specs/")[0].replace("/",
				File.separator);
		parent.setName(new File(projectPath).getAbsolutePath());
	}
}