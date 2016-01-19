package io.github.spharris.ssc;

public class ModuleInfo {
	
	private final String name;
	private final String description;
	private final int version;
	
	ModuleInfo(String name, String description, int version) {
		this.name = name;
		this.description = description;
		this.version = version;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getVersion() {
		return version;
	}
}
