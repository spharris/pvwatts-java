package io.github.spharris.ssc;

/**
 * A <code>Module</code> represents an SSC compute module ("pvwattsv1", for example).
 * 
 * Once a module has been created with {@link io.github.spharris.ssc.Module#loadModule}, you can
 * get information about the available variables, add values for variables, and execute
 * 
 * @author spharris
 */
public class Module {
	
	public static Module loadModule(String modName) {
		return null;
	}
	
	/**
	 * Private constructor to prevent direct instantiation. Use <code>loadModule</code> instead.
	 */
	private Module() {}
}
