package io.github.spharris.ssc.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.google.common.base.Optional;

import io.github.spharris.ssc.ExecutionHandler;
import io.github.spharris.ssc.Module;
import io.github.spharris.ssc.ModuleSummary;
import io.github.spharris.ssc.Variable;
import io.github.spharris.ssc.Variable.VariableType;
import io.github.spharris.ssc.Variable.DataType;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Path("/")
public class ModuleService {
	
	@Context
	private UriInfo uriInfo;
	
	@GET
	public List<ModuleSummary> listModules() {
		List<ModuleSummary> modules = Module.getAvailableModules();
		for (ModuleSummary mi : modules) {
			mi.setUrl(uriInfo.getAbsolutePath() + mi.getName());
		}
		
		return modules;
	}
	
	@GET
	@Path("/{moduleName}")
	public ModuleSummary getModuleInfo(@PathParam("moduleName") String moduleName) {
		Module m = Module.forName(moduleName);
		return m.getModuleInfo();
	}
	
	private static class DefaultHandler implements ExecutionHandler {

		private List<LogMessage> messages;
		
		public DefaultHandler(List<LogMessage> messages) {
			this.messages = messages;
		}
		
		@Override
		public boolean handleLogMessage(MessageType type, float time, String message) {
			messages.add(new LogMessage(type, time, message));
			return true;
		}

		@Override
		public boolean handleProgressUpdate(float percentComplete, float time, String text) {
			return true;
		}
		
	}
	
	@POST
	@Path("/{moduleName}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SimulationResponse moduleSimulation(@PathParam("moduleName") String moduleName,
			Map<String, Object> inputs) {

		Module m = Module.forName(moduleName);
		List<Variable> variables = m.getVariables();
		setVariables(m, variables, inputs);
		
		List<LogMessage> messages = new LinkedList<>();
		ExecutionHandler hndl = new DefaultHandler(messages);
		m.execute(hndl);
		
		SimulationResponse resp = new SimulationResponse();
		resp.setModuleInfo(m.getShortModuleInfo());
		resp.setInputs(inputs);
		resp.setMessages(messages);
		resp.setOutputs(getOutputs(m, variables));
		
		m.free();
		
		return resp;
	}
	
	private static void setVariables(Module mod, List<Variable> variables, Map<String, Object> inputs) {
		for (Variable var : variables) {
			if (var.getVariableType() != VariableType.INPUT) {
				continue;
			}
			
			Object value = inputs.get(var.getName());
			if (value == null) {
				continue;
			}
			
			String name = var.getName();
			switch (var.getDataType()) {
			case NUMBER:
				mod.setValue(name, ((Number)value).floatValue());
				break;
			case STRING:
				mod.setValue(name, (String)value);
				break;
			case ARRAY:
				mod.setValue(name, getArray(value));
				break;
			case MATRIX:
				mod.setValue(name, getMatrix(value));
				break;
			default:
				// Do nothing
			}
		}
	}
	
	private static float[][] getMatrix(Object mtx) {
		@SuppressWarnings("unchecked")
		List<List<Double>> mtxList = (List<List<Double>>)mtx;
		
		float[][] result = new float[mtxList.size()][mtxList.get(0).size()];
		for (int i = 0; i < mtxList.size(); i++) {
			List<Double> row = mtxList.get(i);
			for (int j = 0; j < row.size(); j++) {
				float entry = row.get(j).floatValue();
				result[i][j] = entry;
			}
		}
		
		return result;
	}
	
	private static float[] getArray(Object ary) {
		@SuppressWarnings("unchecked")
		List<Double> aryList = (List<Double>)ary;
		
		float[] result = new float[aryList.size()];
		for (int i = 0; i < aryList.size(); i++) {
			result[i] = aryList.get(i).floatValue();
		}
		
		return result;
	}
	
	private static Map<String, Object> getOutputs(Module mod, List<Variable> variables) {
		Map<String, Object> results = new TreeMap<>();
		for (Variable var : variables) {
			if (var.getVariableType() != VariableType.OUTPUT) {
				continue;
			}			
			
			String name = var.getName();
			Optional<?> value = null;
			switch (var.getDataType()) {
			case NUMBER:
				value = mod.getNumber(name);
				break;
			case STRING:
				value = mod.getString(name);
				break;
			case ARRAY:
				value = mod.getArray(name);
				break;
			case MATRIX:
				value = mod.getMatrix(name);
				break;
			default:
				// Do nothing
			}
			
			if (value.isPresent()) {
				results.put(name, value.get());
			}
		}
		
		return results;
	}
}
