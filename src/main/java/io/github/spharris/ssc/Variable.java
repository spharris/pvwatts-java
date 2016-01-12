package io.github.spharris.ssc;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a module variable.
 */
public class Variable {
	
	public enum VariableType {
		INPUT(1),
		OUTPUT(2),
		INOUT(3);
		
		private int sscConst;
		
		private static Map<Integer, Variable.VariableType> sscVarTypeMap = new HashMap<>();
		static {
			for (Variable.VariableType v : values()) {
				sscVarTypeMap.put(v.sscConst, v);
			}
		}
		
		private VariableType(final int sscConst) {
			this.sscConst = sscConst; 
		}
		
		public static Variable.VariableType forInt(int val) {
			return sscVarTypeMap.get(val);
		}
	}
	
	public enum DataType {
		INVALID(0),
		STRING(1),
		NUMBER(2),
		ARRAY(3),
		MATRIX(4),
		TABLE(5);
		
		private int sscConst;
		
		private static Map<Integer, Variable.DataType> sscDataTypeMap = new HashMap<>();
		static {
			for (Variable.DataType v : values()) {
				sscDataTypeMap.put(v.sscConst, v);
			}
		}
		
		private DataType(final int sscConst) {
			this.sscConst = sscConst; 
		}
		
		public static Variable.DataType forInt(int val) {
			return sscDataTypeMap.get(val);
		}
	}
	
	
}