package io.github.spharris.ssc.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import io.github.spharris.ssc.Module;
import io.github.spharris.ssc.ModuleInfo;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Path("/")
public class ModuleService {
	
	@Context
	private UriInfo uriInfo;
	
	@GET
	public List<ModuleInfo> listModules() {
		List<ModuleInfo> modules = Module.getAvailableModules();
		for (ModuleInfo mi : modules) {
			mi.setUrl(uriInfo.getAbsolutePath() + mi.getName());
		}
		
		return modules;
	}
	
	@GET
	@Path("/{moduleName}")
	public ModuleInfo getModuleInfo(@PathParam("moduleName") String moduleName) {
		Module m = Module.forName(moduleName);
		return m.getModuleInfo();
	}
}
