package io.github.spharris.ssc.web;

import java.util.Map;

public class SimulationRequest {
	private Map<String, Object> inputs;
	
	public void setInputs(Map<String, Object> inputs) {
		this.inputs = inputs;
	}
	
	public Map<String, Object> getInputs() {
		return inputs;
	}
}
