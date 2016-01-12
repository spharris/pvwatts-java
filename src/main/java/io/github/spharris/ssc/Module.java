package io.github.spharris.ssc;

import io.github.spharris.data.Matrix;
import io.github.spharris.ssc.excepions.UnknownModuleNameException;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;

/**
 * A <code>Module</code> represents an SSC compute module ("pvwattsv1", for example).
 * 
 * Once a module has been created with {@link io.github.spharris.ssc.Module#forName}, you can
 * get information about the available variables, add values for variables, and execute
 * 
 * @author spharris
 */
public class Module {

	private String moduleName;
	private Pointer module;
	private Data data;
	private Ssc api;
	
	/**
	 * Creates and returns an SSC compute module with the given name.
	 * 
	 * @throws UnknownModuleNameException if no such module exists.
	 */
	public static Module forName(String modName) {
		Ssc api = loadSscLibrary();
		return new Module(modName, api);
	}
	
	private static Ssc loadSscLibrary() {
		return LibraryLoader.create(Ssc.class)
			.search(getSscPath())
			.load("ssc");
	}
	
	private static String getSscPath() {
		String basePath = Module.class.getClassLoader().getResource("ssc").getPath();
		
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith("mac")) {
			return basePath + "/osx64";
		} else {
			return basePath + "/linux64";
		}
	}
	
	/**
	 * Package-private constructor to prevent direct instantiation. Use <code>forName</code> instead.
	 */
	Module(String moduleName, Ssc api) {
		module = api.ssc_module_create(moduleName);
		if (module == null) {
			throw new UnknownModuleNameException(moduleName);
		}
		
		this.moduleName = moduleName;
		this.api = api;
		data = new Data();
	}
	
	@Override
	public void finalize() {
		api.ssc_module_free(module);
	}
	
	public void setNumber(String variableName, float value) {
		data.setNumber(variableName, value);
	}
	
	/**
	 * Wrapper for an SSC data pointer. Forwards any call to setXX to the appropriate
	 * SSC function
	 */
	private class Data {
		
		private Pointer dataPointer;
		
		public Data() {
			dataPointer = api.ssc_data_create();
		}
		
		public void setNumber(String name, float value) {
			api.ssc_data_set_number(dataPointer, name, value);
		}
		
		public void setString(String name, String value) {
			api.ssc_data_set_string(dataPointer, name, value);
		}
		
		public void setArray(String name, float[] value) {
			api.ssc_data_set_array(dataPointer, name, value, value.length);
		}
		
		public void setMatrix(String name, Matrix value) {
			api.ssc_data_set_matrix(dataPointer, name, null, value.rows(), value.cols());
		}
		
		@Override
		public void finalize() {
			api.ssc_data_free(dataPointer);
		}
		
		
	}
}
