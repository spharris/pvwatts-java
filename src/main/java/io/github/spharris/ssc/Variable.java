package io.github.spharris.ssc;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable class representing a module variable.
 */
public class Variable {
	
	private final VariableType varType;
	private final DataType dataType;
	private final String name;
	private final String label;
	private final String units;
	private final String meta;
	private final String group;
	private final String required;
	
	/**
	 * Since this is a value object that can only come from a module, don't let
	 * people construct it on their own.
	 */
	private Variable(VariableType varType, DataType dataType, String name, String label,
			String units, String meta, String group, String required) {
		this.varType = varType;
		this.dataType = dataType;
		this.name = name;
		this.label = label;
		this.units = units;
		this.meta = meta;
		this.group = group;
		this.required = required;
	}
	
	static Builder buildVariable() {
		return new Builder();
	}
	
	static class Builder {
		private VariableType varType;
		private DataType dataType;
		private String name;
		private String label;
		private String units;
		private String meta;
		private String group;
		private String required;
		
		Builder varType(VariableType value) {
			varType = value;
			return this;
		}
		
		Builder dataType(DataType value) {
			dataType = value;
			return this;
		}
		
		Builder name(String value) {
			name = value;
			return this;
		}
		
		Builder label(String value) {
			label = value;
			return this;
		}
		
		Builder units(String value) {
			units = value;
			return this;
		}
		
		Builder meta(String value) {
			meta = value;
			return this;
		}
		
		Builder group(String value) {
			group = value;
			return this;
		}
		
		Builder required(String value) {
			required = value;
			return this;
		}
		
		Variable build() {
			return new Variable(varType, dataType, name, label, units, meta, group, required);
		}
	}
	
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
	
	public VariableType variableType() {
		return varType;
	}
	
	public DataType dataType() {
		return dataType;
	}
	
	public String name() {
		return name;
	}
	
	public String label() {
		return label;
	}
	
	public String units() {
		return units;
	}
	
	public String meta() {
		return meta;
	}
	
	public String group() {
		return group;
	}
	
	public String required() {
		return required;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Variable)) {
			return false;
		}
		
		Variable otherVar = (Variable)o;
		return otherVar.variableType() == varType
				&& otherVar.dataType() == dataType
				&& Objects.equals(otherVar.name(), name)
				&& Objects.equals(otherVar.label(), label)
				&& Objects.equals(otherVar.units(), units)
				&& Objects.equals(otherVar.meta(), meta)
				&& Objects.equals(otherVar.group(), group)
				&& Objects.equals(otherVar.required(), required);
	}
	
	public int hashCode() {
		return Objects.hash(varType, dataType, name, label, units, meta, group, required);
	}
}