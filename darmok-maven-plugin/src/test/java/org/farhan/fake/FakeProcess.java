package org.farhan.fake;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Test double for {@link Process} used via the {@link ProcessRunner#starter} seam.
 * <p>
 * Supplies canned stdout and a fixed exit code so characterization tests can run
 * without spawning a real subprocess. Optional {@link #withDelay(Duration)} makes
 * {@link #waitFor()} sleep before returning — for simulating long-running Claude
 * invocations (sheep-dog-main#140 timeout/kill/rollback tests).
 */
public class FakeProcess extends Process {

	private final ByteArrayInputStream stdout;
	private final ByteArrayOutputStream stdin = new ByteArrayOutputStream();
	private final int exitCode;
	private Duration delay = Duration.ZERO;

	public FakeProcess(String stdoutText, int exitCode) {
		this.stdout = new ByteArrayInputStream(stdoutText.getBytes(StandardCharsets.UTF_8));
		this.exitCode = exitCode;
	}

	public FakeProcess withDelay(Duration delay) {
		this.delay = delay;
		return this;
	}

	@Override
	public OutputStream getOutputStream() {
		return stdin;
	}

	@Override
	public InputStream getInputStream() {
		return stdout;
	}

	@Override
	public InputStream getErrorStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

	@Override
	public int waitFor() throws InterruptedException {
		if (!delay.isZero()) {
			TimeUnit.MILLISECONDS.sleep(delay.toMillis());
		}
		return exitCode;
	}

	@Override
	public int exitValue() {
		return exitCode;
	}

	@Override
	public void destroy() {
		// no-op
	}
}
