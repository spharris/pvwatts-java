package io.github.spharris.ssc;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.Delegate;

/**
 * Class to be handed to Ssc.ssc_module_exec_with_handler. See ssc_guide.pdf for more details
 * 
 * @author spharris
 */
public interface SscExecutionHandler {
	@Delegate public boolean update(Pointer module, Pointer sscFunction, int action,
			float f0, float f1, String s0, String s1, Pointer userData);
}
