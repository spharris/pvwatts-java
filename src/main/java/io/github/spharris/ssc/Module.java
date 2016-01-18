package io.github.spharris.ssc;

import static com.google.common.base.Preconditions.*;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;

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
	
	private static final int FLOAT_SIZE = 4;

	private String moduleName;
	private Pointer module;
	private Pointer data;
	private Ssc api;
	private List<Variable> variables;
	
	private boolean freed = false;
	
	/**
	 * Creates and returns an SSC compute module with the given name. Created modules
	 * with <tt>forName</tt> use native memory and therefore must be freed when no longer needed.
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
		data = api.ssc_data_create();
	}
	
	/**
	 * Get the name of SSC compute module
	 */
	public String getName() {
		return moduleName;
	}
	
	/**
	 * Returns a list of all of the variables for this module.
	 * 
	 * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
	 */
	public List<Variable> getVariables() {
		checkState();
		
		if (variables != null) {
			return variables;
		}
		
		variables = new LinkedList<>();
		
		int i = 0;
		Pointer infoPointer = api.ssc_module_var_info(module, i);
		while (infoPointer != null) {
			Variable var = Variable.buildVariable()
					.varType(Variable.VariableType.forInt(api.ssc_info_var_type(infoPointer)))
					.dataType(Variable.DataType.forInt(api.ssc_info_data_type(infoPointer)))
					.name(api.ssc_info_name(infoPointer))
					.label(api.ssc_info_label(infoPointer))
					.units(api.ssc_info_units(infoPointer))
					.meta(api.ssc_info_meta(infoPointer))
					.group(api.ssc_info_group(infoPointer))
					.required(api.ssc_info_required(infoPointer))
					.build();
			
			variables.add(var);
			
			i++;
			infoPointer = api.ssc_module_var_info(module, i);
		}
		
		return variables;
	}
	
	/**
	 * Set a numeric input value for this module.
	 * 
	 * @param variableName The name of the variable to set.
	 * @param value The value.
	 * 
	 * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
	 * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
	 */
	public void setNumber(String variableName, float value) {
		checkState();
		checkNotNull(variableName);
		
		api.ssc_data_set_number(data, variableName, value);
	}
	
	/**
	 * Get a numeric input or output number for this module.
	 * 
	 * @param variableName The name of the variable to retrieve.
	 * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
	 * an empty {@link Optional}
	 * 
	 * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
	 * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
	 */
	public Optional<Float> getNumber(String variableName) {
		checkState();
		checkNotNull(variableName);
		
		FloatByReference value = new FloatByReference();
		boolean present = api.ssc_data_get_number(data, variableName, value); 
		
		if (present) {
			return Optional.of(value.floatValue());
		} else {
			return Optional.absent();
		}
	}
	
	/**
	 * Set a String input value for this module.
	 * 
	 * @param variableName The name of the variable to set.
	 * @param value The value.
	 * 
	 * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
	 * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> or <tt>value</tt> is null
	 */
	public void setString(String variableName, String value) {
		checkState();
		checkNotNull(value);
		
		api.ssc_data_set_string(data, variableName, value);
	}
	
	/**
	 * Get a numeric input or output number for this module.
	 * 
	 * @param variableName The name of the variable to retrieve.
	 * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
	 * an empty {@link Optional}
	 * 
	 * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
	 * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
	 */
	public Optional<String> getString(String variableName) {
		checkState();
		checkNotNull(variableName);
		
		String val = api.ssc_data_get_string(data, variableName);
		return Optional.fromNullable(val);
	}
	
	/**
	 * Set an array input value for this module.
	 * 
	 * @param variableName The name of the variable to set.
	 * @param value The value.
	 * 
	 * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
	 * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> or <tt>value</tt> is null
	 * @throws {@link java.lang.IllegalArgumentException} if <tt>value</tt> has a length of zero.
	 */
	public void setArray(String variableName, float[] value) {
		checkState();
		checkNotNull(value);
		checkArgument(value.length >= 1, "The length of the array must be >= 1.");
		
		api.ssc_data_set_array(data, variableName, value, value.length);
	}
	
	/**
	 * Get a numeric input or output number for this module.
	 * 
	 * @param variableName The name of the variable to retrieve.
	 * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
	 * an empty {@link Optional}
	 * 
	 * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
	 * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
	 */
	public Optional<float[]> getArray(String variableName) {
		checkState();
		checkNotNull(variableName);
		
		IntByReference length = new IntByReference();
		Pointer result = api.ssc_data_get_array(data, variableName, length);
		
		if (result == null) {
			return Optional.<float[]>absent();
		} else {
			int len = length.intValue();
			float[] arr = new float[len];
			
			for (int i = 0; i < len; i++) {
				arr[i] = result.getFloat(i * FLOAT_SIZE);
			}
			
			return Optional.of(arr);
		}
	}
	
	/**
	 * Set an array input value for this module.
	 * 
	 * @param variableName The name of the variable to set.
	 * @param value The value.
	 * 
	 * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
	 * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> or <tt>value</tt> is null
	 * @throws {@link java.lang.IllegalArgumentException} if <tt>value</tt> has zero rows or columns
	 */
	public <T extends Number> void setMatrix(String variableName, float[][] value) {
		checkState();
		checkNotNull(value);
		checkArgument(value.length >= 1, "The number of rows must be >= 1.");
		checkArgument(value[0].length >= 1, "The number of columns must be >= 1.");
		
		int rows = value.length;
		int cols = value[0].length;
		float[] inputArray = new float[rows*cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				inputArray[arrayIndex(i, j, cols)] = value[i][j];
			}
		}
		
		api.ssc_data_set_matrix(data, variableName, inputArray, rows, cols);
	}
	
	/**
	 * Get a numeric input or output number for this module.
	 * 
	 * @param variableName The name of the variable to retrieve.
	 * @return If <tt>variableName</tt> is found, an {@link Optional} containing the value. Otherwise,
	 * an empty {@link Optional}
	 * 
	 * @throws {@link java.lang.IllegalStateException} if the module has already been {@link #free}ed.
	 * @throws {@link java.lang.NullPointerException} if <tt>variableName</tt> is null
	 */
	public Optional<float[][]> getMatrix(String variableName) {
		checkState();
		checkNotNull(variableName);
		
		IntByReference rows = new IntByReference();
		IntByReference cols = new IntByReference();
		Pointer result = api.ssc_data_get_matrix(data, variableName, rows, cols);
		
		if (result == null) {
			return Optional.<float[][]>absent();
		} else {
			float[][] value = new float[rows.intValue()][cols.intValue()];
			for (int i = 0; i < rows.intValue(); i++) {
				for (int j = 0; j < rows.intValue(); j++) {
					value[i][j] = result.getFloat(arrayIndex(i, j, cols.intValue()) * FLOAT_SIZE);
				}
			}
			
			return Optional.of(value);
		}
	}
	
	private static int arrayIndex(int i, int j, int cols) {
		return (i * cols) + j;
	}
	
	/**
	 * Frees the underlying pointers associated with this module. Subsequent calls to
	 * other module functions will result in an {@link java.lang.IllegalStateException}
	 */
	public void free() {
		if (!freed) {
			api.ssc_module_free(module);
			api.ssc_data_free(data);
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
}
