package fitnesse.testsystems;

import java.util.LinkedList;
import java.util.List;

/**
 * Send commands to a set of listeners.
 * Misbehaving listeners (the ones that throw IOException's) are removed from the list of listeners.
 */
public class CompositeTestSystemListener implements TestSystemListener {

  private final List<TestSystemListener> listeners = new LinkedList<>();

  public final void addTestSystemListener(TestSystemListener listener) {
    listeners.add(listener);
  }

  protected final List<TestSystemListener> listeners() {
    return listeners;
  }

  @Override
  public void testSystemStarted(final TestSystem testSystem) {
      listeners.forEach((listener) -> {
          listener.testSystemStarted(testSystem);
      });
  }

  @Override
  public void testOutputChunk(final String output) {
      listeners.forEach((listener) -> {
          listener.testOutputChunk(output);
      });
  }

  @Override
  public void testStarted(final TestPage testPage) {
      listeners.forEach((listener) -> {
          listener.testStarted(testPage);
      });
  }

  @Override
  public void testComplete(final TestPage testPage, final TestSummary testSummary) {
      listeners.forEach((listener) -> {
          listener.testComplete(testPage, testSummary);
      });
  }

  @Override
  public void testSystemStopped(final TestSystem testSystem, final Throwable cause) {
      listeners.forEach((listener) -> {
          listener.testSystemStopped(testSystem, cause);
      });
  }

  @Override
  public void testAssertionVerified(Assertion assertion, TestResult testResult) {
      listeners.forEach((listener) -> {
          listener.testAssertionVerified(assertion, testResult);
      });
  }

  @Override
  public void testExceptionOccurred(Assertion assertion, ExceptionResult exceptionResult) {
      listeners.forEach((listener) -> {
          listener.testExceptionOccurred(assertion, exceptionResult);
      });
  }
}
