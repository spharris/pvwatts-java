package io.github.spharris.ssc;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Class to be handed to Ssc.ssc_module_exec_with_handler. See ssc_guide.pdf for more details
 *
 * @author spharris
 */
interface SscExecutionHandler extends Callback {
  public boolean update(
      Pointer module,
      Pointer sscFunction,
      int action,
      float f0,
      float f1,
      String s0,
      String s1,
      Pointer userData);
}
