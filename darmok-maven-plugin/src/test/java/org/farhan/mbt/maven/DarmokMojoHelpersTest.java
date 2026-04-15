package org.farhan.mbt.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

/**
 * Characterization tests for pure and near-pure helpers in {@link DarmokMojo}:
 * scenario list parsing and mutation, asciidoc tag insertion, runner-class source
 * generation, duration formatting, and LF-only file writing.
 * <p>
 * <b>Intent:</b> lock in the observable behavior of the helpers that feed the
 * RGR orchestration. These tests use no mocks and no subprocess — the helpers
 * are either pure functions or operate on the real filesystem via {@code @TempDir}.
 * Each test's Javadoc states the scenario and expected outcome in plain language
 * that will map directly to an asciidoc specification for the helper (#253).
 * <p>
 * Tests instantiate a minimal concrete {@link DarmokMojo} subclass
 * ({@link TestDarmokMojo}) and set package-private fields directly rather than
 * calling {@code init()} (which would require a real MavenProject).
 */
class DarmokMojoHelpersTest {

	@TempDir
	Path workDir;

	TestDarmokMojo mojo;
	CategoryLog mojoLog;

	@BeforeEach
	void setup() throws Exception {
		mojo = new TestDarmokMojo();
		mojo.baseDir = workDir.toString();
		mojo.scenariosFile = "scenarios-list.txt";
		mojo.asciidocDir = "specs";
		mojoLog = new CategoryLog(new SystemStreamLog(), "mojo", workDir.resolve("mojo.log"));
		mojo.mojoLog = mojoLog;
	}

	@AfterEach
	void teardown() {
		if (mojoLog != null) {
			mojoLog.close();
		}
	}

	// =========================================================================
	// formatDuration
	// =========================================================================

	/**
	 * Given: millisecond durations spanning sub-second, sub-minute, sub-hour, and multi-hour ranges.
	 * When: formatDuration is called with each value.
	 * Then: the result is an HH:MM:SS string with two-digit zero-padded fields.
	 * <p>
	 * This is the contract consumed by the METRIC log lines that the PBC report skill
	 * parses to plot phase durations.
	 */
	@Test
	void formatDuration_producesZeroPaddedHoursMinutesSeconds() {
		assertThat(mojo.formatDuration(500)).isEqualTo("00:00:00");
		assertThat(mojo.formatDuration(1_000)).isEqualTo("00:00:01");
		assertThat(mojo.formatDuration(60_000)).isEqualTo("00:01:00");
		assertThat(mojo.formatDuration(3_600_000)).isEqualTo("01:00:00");
		assertThat(mojo.formatDuration(3_723_000)).isEqualTo("01:02:03");
	}

	// =========================================================================
	// parseScenarios
	// =========================================================================

	/**
	 * Given: a scenarios-list file with a single File / Scenario / Tag triple.
	 * When: parseScenarios is called.
	 * Then: exactly one ScenarioEntry is returned with matching fields.
	 */
	@Test
	void parseScenarios_singleTriple_returnsOneEntry() throws Exception {
		Path file = workDir.resolve("single.txt");
		Files.writeString(file, """
				File: features/login.asciidoc
				  Scenario: User logs in successfully
				    Tag: loginHappyPath
				""");

		List<DarmokMojo.ScenarioEntry> entries = mojo.parseScenarios(file.toString());

		assertThat(entries).hasSize(1);
		assertThat(entries.get(0))
			.isEqualTo(new DarmokMojo.ScenarioEntry(
				"features/login.asciidoc",
				"User logs in successfully",
				"loginHappyPath"));
	}

	/**
	 * Given: a scenarios-list file with two scenarios under the same File and a third under a second File.
	 * When: parseScenarios is called.
	 * Then: three entries are returned; the first two carry the first file, the third carries the second.
	 * <p>
	 * This characterizes the File-header-sticks-until-replaced parsing contract.
	 */
	@Test
	void parseScenarios_multipleFilesAndScenarios_associatesCorrectly() throws Exception {
		Path file = workDir.resolve("multi.txt");
		Files.writeString(file, """
				File: features/a.asciidoc
				  Scenario: First
				    Tag: tagOne
				  Scenario: Second
				    Tag: tagTwo
				File: features/b.asciidoc
				  Scenario: Third
				    Tag: tagThree
				""");

		List<DarmokMojo.ScenarioEntry> entries = mojo.parseScenarios(file.toString());

		assertThat(entries).hasSize(3);
		assertThat(entries.get(0).file()).isEqualTo("features/a.asciidoc");
		assertThat(entries.get(0).scenario()).isEqualTo("First");
		assertThat(entries.get(0).tag()).isEqualTo("tagOne");
		assertThat(entries.get(1).file()).isEqualTo("features/a.asciidoc");
		assertThat(entries.get(1).scenario()).isEqualTo("Second");
		assertThat(entries.get(1).tag()).isEqualTo("tagTwo");
		assertThat(entries.get(2).file()).isEqualTo("features/b.asciidoc");
		assertThat(entries.get(2).scenario()).isEqualTo("Third");
		assertThat(entries.get(2).tag()).isEqualTo("tagThree");
	}

	// =========================================================================
	// generateRunnerClassContent
	// =========================================================================

	/**
	 * Given: a pattern and runner class name.
	 * When: generateRunnerClassContent is called.
	 * Then: the generated Java source declares the class, imports Suite annotations,
	 *   and includes an @IncludeTags that matches the pattern.
	 * <p>
	 * This is what the red phase writes under src/test/java/.../suites/; failures
	 * here would produce uncompilable runner classes and break rgr-red.
	 */
	@Test
	void generateRunnerClassContent_includesSuiteAnnotationsAndTag() {
		String source = mojo.generateRunnerClassContent("loginHappyPath", "LoginHappyPathTest");

		assertThat(source)
			.contains("package org.farhan.suites;")
			.contains("import org.junit.platform.suite.api.Suite;")
			.contains("@Suite")
			.contains("@IncludeEngines(\"cucumber\")")
			.contains("@IncludeTags(\"loginHappyPath\")")
			.contains("public class LoginHappyPathTest {");
	}

	// =========================================================================
	// writeFileWithLF
	// =========================================================================

	/**
	 * Given: a list of lines on a Windows host (where the default line separator is CRLF).
	 * When: writeFileWithLF is called.
	 * Then: the on-disk bytes contain only LF separators (no CR), and the file ends with a trailing LF.
	 * <p>
	 * This characterizes the checked-in-file-format contract: generated runner classes
	 * and rewritten asciidoc files must use LF regardless of host OS so diffs stay clean
	 * across Windows and Linux CI runners.
	 */
	@Test
	void writeFileWithLF_writesLFOnly_evenOnWindows() throws Exception {
		Path out = workDir.resolve("out.txt");

		mojo.writeFileWithLF(out.toString(), List.of("alpha", "beta", "gamma"));

		byte[] bytes = Files.readAllBytes(out);
		String content = new String(bytes, StandardCharsets.UTF_8);
		assertThat(content).isEqualTo("alpha\nbeta\ngamma\n");
		assertThat(bytes).doesNotContain((byte) '\r');
	}

	// =========================================================================
	// removeFirstScenarioFromFile
	// =========================================================================

	/**
	 * Given: a scenarios-list file containing exactly one File / Scenario / Tag triple.
	 * When: removeFirstScenarioFromFile is called.
	 * Then: the file is left empty (all lines consumed).
	 */
	@Test
	void removeFirstScenarioFromFile_onlyEntry_clearsFile() throws Exception {
		Path file = workDir.resolve("scenarios-list.txt");
		Files.writeString(file, """
				File: features/a.asciidoc
				  Scenario: First
				    Tag: tagOne
				""");

		mojo.removeFirstScenarioFromFile();

		assertThat(Files.readString(file)).isEmpty();
	}

	/**
	 * Given: a scenarios-list file with two scenarios under the same File header and the first is removed.
	 * When: removeFirstScenarioFromFile is called.
	 * Then: the remaining file still starts with the File header and the second scenario is preserved.
	 * <p>
	 * This characterizes the sticky-File-header invariant — the parser relies on every
	 * Scenario having a preceding File header, so removal must not orphan the second scenario.
	 */
	@Test
	void removeFirstScenarioFromFile_secondScenarioSameFile_preservesFileHeader() throws Exception {
		Path file = workDir.resolve("scenarios-list.txt");
		Files.writeString(file, """
				File: features/a.asciidoc
				  Scenario: First
				    Tag: tagOne
				  Scenario: Second
				    Tag: tagTwo
				""");

		mojo.removeFirstScenarioFromFile();

		String remaining = Files.readString(file);
		assertThat(remaining)
			.startsWith("File: features/a.asciidoc\n")
			.contains("Scenario: Second")
			.contains("Tag: tagTwo")
			.doesNotContain("Scenario: First")
			.doesNotContain("Tag: tagOne");
	}

	// =========================================================================
	// getNextScenario
	// =========================================================================

	/**
	 * Given: no scenarios-list file exists at the configured path.
	 * When: getNextScenario is called.
	 * Then: null is returned (not an exception).
	 * <p>
	 * This characterizes the nothing-to-do contract — the mojo treats a missing
	 * file as "work queue is empty" so CI can run the plugin unconditionally without
	 * needing a guard for first-time runs.
	 */
	@Test
	void getNextScenario_missingFile_returnsNull() throws Exception {
		DarmokMojo.ScenarioEntry entry = mojo.getNextScenario();

		assertThat(entry).isNull();
	}

	/**
	 * Given: the scenarios-list file exists but is empty.
	 * When: getNextScenario is called.
	 * Then: null is returned.
	 * <p>
	 * Same nothing-to-do contract as the missing-file case — parseScenarios returns
	 * an empty list, which getNextScenario normalizes to null so callers have a
	 * single "no work" sentinel to check.
	 */
	@Test
	void getNextScenario_emptyFile_returnsNull() throws Exception {
		Files.writeString(workDir.resolve(mojo.scenariosFile), "");

		DarmokMojo.ScenarioEntry entry = mojo.getNextScenario();

		assertThat(entry).isNull();
	}

	/**
	 * Given: the scenarios-list file contains multiple File / Scenario / Tag triples.
	 * When: getNextScenario is called.
	 * Then: the first entry in file order is returned.
	 * <p>
	 * This characterizes the FIFO processing contract — scenarios are worked in
	 * the order they appear in the file, not alphabetically or by tag.
	 */
	@Test
	void getNextScenario_multipleEntries_returnsFirstInOrder() throws Exception {
		Files.writeString(workDir.resolve(mojo.scenariosFile), """
				File: features/a.asciidoc
				  Scenario: First
				    Tag: tagOne
				  Scenario: Second
				    Tag: tagTwo
				""");

		DarmokMojo.ScenarioEntry entry = mojo.getNextScenario();

		assertThat(entry).isEqualTo(new DarmokMojo.ScenarioEntry(
			"features/a.asciidoc", "First", "tagOne"));
	}

	// =========================================================================
	// addTagToAsciidoc
	// =========================================================================

	/**
	 * Given: an asciidoc file whose Test-Case has no tag line yet.
	 * When: addTagToAsciidoc is called with a fresh tag.
	 * Then: a new "@tag" line is inserted after the Test-Case header preceded by a blank line
	 *   and the method returns true (change applied).
	 */
	@Test
	void addTagToAsciidoc_noExistingTagLine_insertsNewTagLine() throws Exception {
		Path specFile = writeSpec("login", """
				== Test-Case: User logs in successfully

				Some description
				""");

		boolean changed = mojo.addTagToAsciidoc("login", "User logs in successfully", "loginHappyPath");

		assertThat(changed).isTrue();
		String content = Files.readString(specFile);
		assertThat(content)
			.contains("== Test-Case: User logs in successfully")
			.contains("\n@loginHappyPath\n");
	}

	/**
	 * Given: an asciidoc file whose Test-Case already has a tag line with a different tag.
	 * When: addTagToAsciidoc is called with a new tag.
	 * Then: the new tag is appended to the existing tag line (space-separated) and the method returns true.
	 */
	@Test
	void addTagToAsciidoc_existingTagLine_appendsNewTag() throws Exception {
		Path specFile = writeSpec("login", """
				== Test-Case: User logs in successfully

				@existingTag
				Some description
				""");

		boolean changed = mojo.addTagToAsciidoc("login", "User logs in successfully", "loginHappyPath");

		assertThat(changed).isTrue();
		String content = Files.readString(specFile);
		assertThat(content).contains("@existingTag @loginHappyPath");
	}

	/**
	 * Given: an asciidoc file whose Test-Case already carries the target tag.
	 * When: addTagToAsciidoc is called with that same tag.
	 * Then: no change is made and the method returns false (idempotent no-op).
	 * <p>
	 * This characterizes the idempotency contract — Darmok may re-run the same scenario
	 * and must not produce duplicate tag entries that would break downstream tools.
	 */
	@Test
	void addTagToAsciidoc_tagAlreadyPresent_isNoOp() throws Exception {
		Path specFile = writeSpec("login", """
				== Test-Case: User logs in successfully

				@loginHappyPath
				Some description
				""");
		String before = Files.readString(specFile);

		boolean changed = mojo.addTagToAsciidoc("login", "User logs in successfully", "loginHappyPath");

		assertThat(changed).isFalse();
		assertThat(Files.readString(specFile)).isEqualTo(before);
	}

	// =========================================================================
	// deleteNulFiles
	// =========================================================================

	/**
	 * Given: a directory tree containing files named "NUL" (Windows reserved device
	 *   name that can leak into the checkout when scripts redirect to /dev/null).
	 * When: deleteNulFiles is called with the tree root.
	 * Then: every NUL / nul file is removed, non-NUL siblings are preserved,
	 *   and the returned count equals the number deleted.
	 * <p>
	 * This characterizes the Windows-footgun cleanup contract (see CLAUDE.md note
	 * on /dev/null vs NUL). A regression here would silently leak phantom NUL files
	 * into commits.
	 * <p>
	 * Disabled on Windows: the Win32 filesystem layer redirects writes to paths
	 * literally named "NUL" to the NUL device, so the fixture cannot be created
	 * through the standard {@link Files} API. The real bug this method addresses
	 * happens when files named NUL arrive via cygwin/git-bash {@code 2>/dev/null}
	 * translation. Linux CI exercises this path normally.
	 */
	@Test
	@DisabledOnOs(OS.WINDOWS)
	void deleteNulFiles_removesOnlyNulEntries_andReturnsCount() throws Exception {
		Path root = workDir.resolve("tree");
		Files.createDirectories(root.resolve("sub"));
		Files.writeString(root.resolve("NUL"), "junk");
		Files.writeString(root.resolve("sub").resolve("nul"), "junk");
		Files.writeString(root.resolve("keep.txt"), "legit");

		int deleted = mojo.deleteNulFiles(root);

		assertThat(deleted).isEqualTo(2);
		assertThat(Files.exists(root.resolve("NUL"))).isFalse();
		assertThat(Files.exists(root.resolve("sub/nul"))).isFalse();
		assertThat(Files.exists(root.resolve("keep.txt"))).isTrue();
	}

	/**
	 * Given: a directory tree with no NUL-named entries.
	 * When: deleteNulFiles is called.
	 * Then: the returned count is zero and nothing is deleted.
	 */
	@Test
	void deleteNulFiles_noMatches_returnsZero() throws Exception {
		Path root = workDir.resolve("clean");
		Files.createDirectories(root);
		Files.writeString(root.resolve("file.txt"), "legit");

		int deleted = mojo.deleteNulFiles(root);

		assertThat(deleted).isZero();
		assertThat(Files.exists(root.resolve("file.txt"))).isTrue();
	}

	// =========================================================================
	// deleteDirectory
	// =========================================================================

	/**
	 * Given: a nested directory tree with files at multiple depths.
	 * When: deleteDirectory is called with the tree root.
	 * Then: the entire tree is removed (files first, then containing directories).
	 * <p>
	 * This characterizes the target/ wipe used during cleanup between RGR cycles —
	 * the whole Maven build output must be removable even when it contains nested
	 * class files and subdirectories.
	 */
	@Test
	void deleteDirectory_removesEntireNestedTree() throws Exception {
		Path root = workDir.resolve("target");
		Files.createDirectories(root.resolve("classes/org/farhan"));
		Files.writeString(root.resolve("classes/Main.class"), "bytes");
		Files.writeString(root.resolve("classes/org/farhan/X.class"), "bytes");
		Files.writeString(root.resolve("build.log"), "log");

		mojo.deleteDirectory(root);

		assertThat(Files.exists(root)).isFalse();
	}

	/**
	 * Given: a directory path that does not exist.
	 * When: deleteDirectory is called.
	 * Then: the method returns without throwing.
	 * <p>
	 * This characterizes the safe-to-call-on-missing-directory contract — runCleanUp
	 * invokes this unconditionally and must tolerate a first-time run where target/
	 * has never been created.
	 */
	@Test
	void deleteDirectory_missingPath_isNoOp() throws Exception {
		Path missing = workDir.resolve("never-existed");

		mojo.deleteDirectory(missing);

		assertThat(Files.exists(missing)).isFalse();
	}

	// =========================================================================
	// Helpers
	// =========================================================================

	private Path writeSpec(String baseName, String content) throws Exception {
		Path specDir = workDir.resolve(mojo.asciidocDir);
		Files.createDirectories(specDir);
		Path specFile = specDir.resolve(baseName + ".asciidoc");
		Files.writeString(specFile, content);
		return specFile;
	}

	/**
	 * Minimal concrete subclass of the abstract {@link DarmokMojo} so tests can
	 * instantiate and exercise its helpers. {@link #execute()} is a no-op because
	 * these tests never invoke the mojo through Maven's plugin container.
	 */
	static class TestDarmokMojo extends DarmokMojo {
		@Override
		public void execute() throws MojoExecutionException {
			// no-op — helpers under test are invoked directly
		}
	}
}
