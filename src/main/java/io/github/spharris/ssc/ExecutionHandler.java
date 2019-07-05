package io.github.spharris.ssc;

import java.util.HashMap;
import java.util.Map;

public interface ExecutionHandler {
  public static enum MessageType {
    INPUT_ERROR(0),
    NOTICE(1),
    WARNING(2),
    ERROR(3);

    private int sscConst;

    private static Map<Integer, MessageType> sscVarTypeMap = new HashMap<>();

    static {
      for (MessageType v : values()) {
        sscVarTypeMap.put(v.sscConst, v);
      }
    }

    private MessageType(final int sscConst) {
      this.sscConst = sscConst;
    }

    public static MessageType forInt(int val) {
      return sscVarTypeMap.get(val);
    }
  }

  public boolean handleLogMessage(MessageType type, float time, String message);

  public boolean handleProgressUpdate(float percentComplete, float time, String text);
}
