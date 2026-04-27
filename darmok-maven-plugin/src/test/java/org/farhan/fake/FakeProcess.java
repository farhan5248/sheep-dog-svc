package org.farhan.fake;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test double for {@link Process} used via the {@link ProcessRunner#starter} seam.
 * <p>
 * Supplies canned stdout and a fixed exit code so characterization tests can run
 * without spawning a real subprocess. Supports two test-only flags:
 * <ul>
 *   <li>{@link #withDelay(Duration)} — {@link #waitFor()} sleeps before returning;
 *       used by sheep-dog-main issue 140 exploratory tests.</li>
 *   <li>{@link #withHang()} — {@link #waitFor(long, TimeUnit)} returns {@code false}
 *       to simulate a claude subprocess exceeding its timeout budget; subsequent
 *       {@link #destroyForcibly()} flips the hung flag so reap-side calls return.
 *       Used by the issue 140 timeout/kill tests.</li>
 * </ul>
 */
public class FakeProcess extends Process {

	private final ByteArrayInputStream stdout;
	private final String stdoutText;
	private final ByteArrayOutputStream stdin = new ByteArrayOutputStream();
	private final int exitCode;
	private Duration delay = Duration.ZERO;
	private boolean hangs;
	private boolean destroyed;
	private boolean blocksReader;
	private final CountDownLatch readerRelease = new CountDownLatch(1);

	public FakeProcess(String stdoutText, int exitCode) {
		this.stdoutText = stdoutText;
		this.stdout = new ByteArrayInputStream(stdoutText.getBytes(StandardCharsets.UTF_8));
		this.exitCode = exitCode;
	}

	// --- capture-side accessors used by FakeCaptureInterceptor ---
	public int captureExit() { return exitCode; }
	public String captureStdout() { return stdoutText; }
	public boolean captureHangs() { return hangs; }
	public boolean captureBlocksReader() { return blocksReader; }

	public FakeProcess withDelay(Duration delay) {
		this.delay = delay;
		return this;
	}

	public FakeProcess withHang() {
		this.hangs = true;
		return this;
	}

	/**
	 * Marks this FakeProcess as one whose stdout pipe stays open past waitFor
	 * returning — models the issue 290 failure mode where claude.cmd parent
	 * exits but a grandchild keeps the inherited pipe alive silently. The
	 * blocking read releases once {@link #destroyForcibly()} is called.
	 */
	public FakeProcess withBlockedReader() {
		this.blocksReader = true;
		return this;
	}

	@Override
	public OutputStream getOutputStream() {
		return stdin;
	}

	@Override
	public InputStream getInputStream() {
		if (blocksReader) {
			return new InputStream() {
				@Override
				public int read() throws IOException {
					try {
						readerRelease.await();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						throw new IOException(e);
					}
					return -1; // EOF once released
				}
			};
		}
		return stdout;
	}

	@Override
	public InputStream getErrorStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

	@Override
	public int waitFor() throws InterruptedException {
		if (hangs && !destroyed) {
			// Only reached if the caller skipped waitFor(timeout, unit). Tests that
			// exercise the hang path always go through the bounded variant, so
			// arriving here is a bug — fail loudly rather than block forever.
			throw new IllegalStateException("FakeProcess: waitFor() called on hung process without timeout");
		}
		if (!delay.isZero()) {
			TimeUnit.MILLISECONDS.sleep(delay.toMillis());
		}
		return exitCode;
	}

	@Override
	public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
		if (hangs && !destroyed) {
			// Simulate "process didn't complete within timeout" without actually
			// sleeping the full duration — tests stay fast.
			return false;
		}
		return true;
	}

	@Override
	public int exitValue() {
		if (hangs && !destroyed) {
			throw new IllegalThreadStateException("process hasn't exited");
		}
		return exitCode;
	}

	@Override
	public void destroy() {
		destroyed = true;
		readerRelease.countDown();
	}

	@Override
	public Process destroyForcibly() {
		destroyed = true;
		readerRelease.countDown();
		return this;
	}
}
