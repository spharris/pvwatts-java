package io.github.spharris.ssc.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.github.spharris.ssc.Module;
import io.github.spharris.ssc.ModuleInfo;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Path("/")
public class ModuleService {
	
	@GET
	public List<ModuleInfo> listModules() {
		return Module.getAvailableModules();
	}
}
