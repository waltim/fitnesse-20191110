package fitnesse.testsystems;

import java.util.LinkedList;
import java.util.List;

public class CompositeExecutionLogListener implements ExecutionLogListener {

  private final List<ExecutionLogListener> listeners = new LinkedList<>();

  public final void addExecutionLogListener(ExecutionLogListener listener) {
    listeners.add(listener);
  }

  protected final List<ExecutionLogListener> listeners() {
    return listeners;
  }

  @Override
  public void commandStarted(ExecutionContext context) {
      listeners.forEach((listener) -> {
          listener.commandStarted(context);
      });
  }

  @Override
  public void stdOut(String output) {
      listeners.forEach((listener) -> {
          listener.stdOut(output);
      });
  }

  @Override
  public void stdErr(String output) {
      listeners.forEach((listener) -> {
          listener.stdErr(output);
      });
  }

  @Override
  public void exitCode(int exitCode) {
      listeners.forEach((listener) -> {
          listener.exitCode(exitCode);
      });
  }

  @Override
  public void exceptionOccurred(Throwable e) {
      listeners.forEach((listener) -> {
          listener.exceptionOccurred(e);
      });
  }
}
