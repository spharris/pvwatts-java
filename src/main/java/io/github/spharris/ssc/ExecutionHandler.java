package io.github.spharris.ssc;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;

public interface ExecutionHandler {
  enum MessageType {
    INPUT_ERROR(0),
    NOTICE(1),
    WARNING(2),
    ERROR(3);

    private int sscConst;

    private static final ImmutableMap<Integer, MessageType> VAR_TYPE_MAP =
        Arrays.stream(values())
        .collect(toImmutableMap(t -> t.sscConst, t -> t));

    MessageType(final int sscConst) {
      this.sscConst = sscConst;
    }

    public static MessageType forInt(int val) {
      return VAR_TYPE_MAP.get(val);
    }
  }

  boolean handleLogMessage(MessageType type, float time, String message);

  boolean handleProgressUpdate(float percentComplete, float time, String text);
}
