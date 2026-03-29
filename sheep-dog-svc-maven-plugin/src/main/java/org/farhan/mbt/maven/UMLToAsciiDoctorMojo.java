package org.farhan.mbt.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.springframework.stereotype.Component;

/**
 * Converts Graph model paths to Ascii Doctor files
 *
 */
@Mojo(name = "uml-to-asciidoctor", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Component
public class UMLToAsciiDoctorMojo extends MBTMojo {

	public void execute() throws MojoExecutionException {
		super.execute("asciidoctor/", "ConvertUMLToAsciidoctor");
	}
}