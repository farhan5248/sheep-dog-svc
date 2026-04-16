package org.farhan.impl;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.farhan.objects.codeprj.target.darmok.DarmokMojoLogFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Impl for {@code code-prj/target/darmok/darmok.mojo.log}.
 *
 * <p>
 * The real on-disk filename includes today's date (e.g. {@code darmok.mojo.2026-04-15.log})
 * because {@link org.farhan.mbt.maven.DarmokMojo#initLogs} dates the filename per-run.
 * {@link #resolveFilePath()} maps the spec's stable name {@code darmok.mojo.log} to
 * whichever dated file currently exists in the target/darmok directory.
 *
 * <p>
 * Column-specific getters ({@link #getLevel}, {@link #getCategory}, {@link #getContent})
 * are stubs until the log-content path lands in a later Test-Case.
 */
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class DarmokMojoLogFileImpl extends AbstractFileImpl implements DarmokMojoLogFile {

	@Override
	protected Path resolveFilePath() {
		Path logDir = (Path) getProperty("log.dir");
		if (logDir == null) {
			Object baseDir = getProperty(component + ".baseDir");
			if (baseDir == null) {
				return null;
			}
			logDir = ((Path) baseDir).resolve("target").resolve("darmok");
		}
		return findDatedLog(logDir, "darmok.mojo");
	}

	static Path findDatedLog(Path logDir, String prefix) {
		if (!Files.isDirectory(logDir)) {
			return logDir.resolve(prefix + ".log");
		}
		try (var stream = Files.list(logDir)) {
			return stream
				.filter(p -> {
					String name = p.getFileName().toString();
					return name.startsWith(prefix + ".") && name.endsWith(".log");
				})
				.findFirst()
				.orElse(logDir.resolve(prefix + ".log"));
		} catch (IOException e) {
			return logDir.resolve(prefix + ".log");
		}
	}

	@Override
	public String getLevel(HashMap<String, String> keyMap) {
		return keyMap.get("Level");
	}

	@Override
	public String getCategory(HashMap<String, String> keyMap) {
		return keyMap.get("Category");
	}

	@Override
	public String getContent(HashMap<String, String> keyMap) {
		return keyMap.get("Content");
	}
}
