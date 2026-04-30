package org.farhan.mock;

import org.farhan.dsl.grammar.StepObjectRefFragments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(TestStepNameParser.class);

    public static String getStepObjectName(String fullStepName) {
        String result = StepObjectRefFragments.getAll(fullStepName);
        logger.debug("getStepObjectName fullStepName={} stepObjectName={}", fullStepName, result);
        return result;
    }

    public static String getStepDefinitionName(String fullStepName) {
        String stepObjectName = StepObjectRefFragments.getAll(fullStepName);
        String result = fullStepName.substring(stepObjectName.length()).trim();
        logger.debug("getStepDefinitionName fullStepName={} stepObjectName={} stepDefinitionName={}",
                fullStepName, stepObjectName, result);
        return result;
    }
}
