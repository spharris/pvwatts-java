package io.github.spharris.ssc;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import com.google.common.base.Optional;

import io.github.spharris.data.Matrix;
import io.github.spharris.ssc.excepions.UnknownModuleNameException;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.byref.FloatByReference;
import jnr.ffi.byref.IntByReference;

/**
 * A <code>Module</code> represents an SSC compute module ("pvwattsv1", for example).
 * 
 * Once a module has been created with {@link io.github.spharris.ssc.Module#forName}, you can
 * get information about the available variables, add values for variables, and execute.
 * 
 * <strong>When you are done with a module, it must be freed using {@link #free}. 
 * 
 * @author spharris
 */
public class Module {

	private String moduleName;
	private Pointer module;
	private Data data;
	private Ssc api;
	
	private boolean freed = false;
	
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
	
	public String getName() {
		return moduleName;
	}
	
	public void setNumber(String variableName, float value) {
		checkState();
		data.setNumber(variableName, value);
	}
	
	public Optional<Float> getNumber(String variableName) {
		checkState();
		return data.getNumber(variableName);
	}
	
	public void setString(String variableName, String value) {
		checkState();
		checkNotNull(value);
		data.setString(variableName, value);
	}
	
	public Optional<String> getString(String variableName) {
		checkState();
		return data.getString(variableName);
	}
	
	public void setArray(String variableName, float[] value) {
		checkState();
		data.setArray(variableName, value);
	}
	
	public Optional<Float[]> getArray(String variableName) {
		checkState();
		return data.getArray(variableName);
	}
	
	/**
	 * Frees the underlying pointer associated with this module. Subsequent calls to
	 * other module functions will result in an {@link java.lang.IllegalStateException}
	 */
	public void free() {
		if (!freed) {
			api.ssc_module_free(module);
			data.free();
			freed = true;
		}
	}
	
	public boolean isFreed() {
		return freed;
	}
	
	private void checkState() {
		if (freed) {
			throw new IllegalStateException("The module has already been freed.");
		}
	}
	
	/**
	 * Wrapper for an SSC data pointer. Forwards any call to setXX to the appropriate
	 * SSC function
	 */
	private class Data {
		
		private static final int FLOAT_SIZE = 4;
		private Pointer dataPointer;
		
		
		public Data() {
			dataPointer = api.ssc_data_create();
		}
		
		public void setNumber(String name, float value) {
			api.ssc_data_set_number(dataPointer, name, value);
		}
		
		public Optional<Float> getNumber(String name) {
			FloatByReference value = new FloatByReference();
			boolean present = api.ssc_data_get_number(dataPointer, name, value); 
			
			if (present) {
				return Optional.of(value.floatValue());
			} else {
				return Optional.absent();
			}
		}
		
		public void setString(String name, String value) {
			api.ssc_data_set_string(dataPointer, name, value);
		}
		
		public Optional<String> getString(String name) {
			String val = api.ssc_data_get_string(dataPointer, name);
			
			return Optional.fromNullable(val);
		}
		
		public void setArray(String name, float[] value) {
			api.ssc_data_set_array(dataPointer, name, value, value.length);
		}
		
		public Optional<Float[]> getArray(String name) {
			IntByReference length = new IntByReference();
			Pointer result = api.ssc_data_get_array(dataPointer, name, length);
			
			if (result == null) {
				return Optional.<Float[]>absent();
			} else {
				int len = length.intValue();
				Float[] arr = new Float[len];
				
				for (int i = 0; i < len; i++) {
					arr[i] = result.getFloat(i * FLOAT_SIZE);
				}
				
				return Optional.of(arr);
			}
		}
		
		public void setMatrix(String name, Matrix value) {
			api.ssc_data_set_matrix(dataPointer, name, null, value.rows(), value.cols());
		}
		
		public void free() {
			api.ssc_data_free(dataPointer);
		}
	}
}
