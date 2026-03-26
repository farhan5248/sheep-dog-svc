package org.farhan.dsl.asciidoc.generator;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.OutputConfiguration;

public class AsciiDocOutputConfigurationProvider implements IOutputConfigurationProvider {

	public static OutputConfiguration stepDefsOutput;

	@Override
	public Set<OutputConfiguration> getOutputConfigurations() {

		HashSet<OutputConfiguration> set = new HashSet<OutputConfiguration>();

		OutputConfiguration defaultOutput = new OutputConfiguration(IFileSystemAccess.DEFAULT_OUTPUT);
		defaultOutput.setDescription("Output Folder");
		defaultOutput.setOutputDirectory("./src-gen");
		defaultOutput.setOverrideExistingResources(true);
		defaultOutput.setCreateOutputDirectory(true);
		defaultOutput.setCleanUpDerivedResources(true);
		defaultOutput.setSetDerivedProperty(true);
		set.add(defaultOutput);

		stepDefsOutput = new OutputConfiguration("STEP_DEFS");
		stepDefsOutput.setDescription("Step Definitions");
		stepDefsOutput.setOutputDirectory("./src/test/resources/asciidoc/stepdefs");
		stepDefsOutput.setOverrideExistingResources(true);
		stepDefsOutput.setCreateOutputDirectory(true);
		stepDefsOutput.setCleanUpDerivedResources(false);
		stepDefsOutput.setSetDerivedProperty(false);
		set.add(stepDefsOutput);
		return set;
	}

}
