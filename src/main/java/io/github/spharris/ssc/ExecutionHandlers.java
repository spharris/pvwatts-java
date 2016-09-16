package io.github.spharris.ssc;

import com.google.common.collect.ImmutableList;

/**
 * Implemtations of {@link ExecutionHandler}. 
 */
public final class ExecutionHandlers {
  
  private ExecutionHandlers() {}
  
  public static ExecutionHandler messageLoggingHandler(
      final ImmutableList.Builder<String> errorListBuilder,
      final ImmutableList.Builder<String> warningListBuilder) {
    return new ExecutionHandler() {

      @Override
      public boolean handleLogMessage(MessageType type, float time, String message) {
        if (type == MessageType.ERROR || type == MessageType.NOTICE) {
          errorListBuilder.add(message);
        } else if (type == MessageType.WARNING) {
          warningListBuilder.add(message);
        }
        
        return true;
      }

      @Override
      public boolean handleProgressUpdate(float percentComplete, float time, String text) {
        return true;
      }
    };
  }
}
