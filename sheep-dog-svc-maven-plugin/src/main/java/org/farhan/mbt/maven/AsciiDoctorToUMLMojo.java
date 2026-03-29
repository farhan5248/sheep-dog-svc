package org.farhan.mbt.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.springframework.stereotype.Component;

/**
 * Converts Ascii Doctor files to Graph model paths
 *
 */
@Mojo(name = "asciidoctor-to-uml", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Component
public class AsciiDoctorToUMLMojo extends MBTMojo {

	public void execute() throws MojoExecutionException {
		// TODO test that asciidoc is valid but nothing else
		super.execute("asciidoctor/", "ConvertAsciidoctorToUML", ".asciidoc");
	}
}