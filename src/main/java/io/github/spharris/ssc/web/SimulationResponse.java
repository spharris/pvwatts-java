package io.github.spharris.ssc.web;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.github.spharris.ssc.ModuleInfo;

@JsonPropertyOrder({"moduleInfo", "messages", "inputs", "outputs"})
public final class SimulationResponse {
	private ModuleInfo moduleInfo;
	private Map<String, Object> inputs;
	private Map<String, Object> outputs;
	private List<LogMessage> messages;
	
	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}
	
	public void setModuleInfo(ModuleInfo info) {
		this.moduleInfo = info;
	}
	
	public Map<String, Object> getInputs() {
		return inputs;
	}
	public void setInputs(Map<String, Object> inputs) {
		this.inputs = inputs;
	}
	public Map<String, Object> getOutputs() {
		return outputs;
	}
	public void setOutputs(Map<String, Object> outputs) {
		this.outputs = outputs;
	}
	public List<LogMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<LogMessage> messages) {
		this.messages = messages;
	}
}
