package org.farhan.mock;

import org.farhan.dsl.grammar.StepObjectRefFragments;

/**
 * Mock for the LSP parser service's step-name splitting.
 *
 * <p>
 * In the real xtext grammar, a test step's full name like
 * "The daily batchjob Input file is set as follows" is parsed into two named
 * fields on the {@code TestStep} EMF object: {@code stepObjectName} ("The
 * daily batchjob Input file") and {@code stepDefinitionName} ("is set as
 * follows"). This class performs the same split for code paths that only
 * receive the concatenated full name.
 *
 * <p>
 * Delegates the object-ref matching to the grammar-owned
 * {@link StepObjectRefFragments#getAll(String)}, so the split stays in sync
 * with the xtext grammar rules. Stateless.
 */
public class TestStepNameParser {

    public static String getStepObjectName(String fullStepName) {
        return StepObjectRefFragments.getAll(fullStepName);
    }

    public static String getStepDefinitionName(String fullStepName) {
        String stepObjectName = StepObjectRefFragments.getAll(fullStepName);
        return fullStepName.substring(stepObjectName.length()).trim();
    }
}
