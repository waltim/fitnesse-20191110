package fitnesse.testrunner;

import java.io.Closeable;

import fitnesse.testsystems.CompositeTestSystemListener;
import fitnesse.testsystems.TestSystemListener;
import util.FileUtil;

public class CompositeFormatter extends CompositeTestSystemListener implements TestsRunnerListener, Closeable {

  @Override
  public void announceNumberTestsToRun(int testsToRun) {
      listeners().stream().filter((listener) -> (listener instanceof TestsRunnerListener)).forEachOrdered((listener) -> {
          ((TestsRunnerListener) listener).announceNumberTestsToRun(testsToRun);
      });
  }

  @Override
  public void unableToStartTestSystem(final String testSystemName, final Throwable cause) {
      listeners().stream().filter((listener) -> (listener instanceof TestsRunnerListener)).forEachOrdered((listener) -> {
          ((TestsRunnerListener) listener).unableToStartTestSystem(testSystemName, cause);
      });
  }

  @Override
  public void close() {
      listeners().stream().filter((listener) -> (listener instanceof Closeable)).forEachOrdered((listener) -> {
          FileUtil.close((Closeable) listener);
      });
  }
}
