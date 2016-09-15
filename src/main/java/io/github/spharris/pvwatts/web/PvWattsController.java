package io.github.spharris.pvwatts.web;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.google.common.collect.ImmutableMultimap;

import io.github.spharris.pvwatts.service.PvWatts4Response;
import io.github.spharris.pvwatts.service.PvWatts4Service;
import io.github.spharris.pvwatts.service.PvWatts5Response;
import io.github.spharris.pvwatts.service.PvWatts5Service;

@Path("/")
public final class PvWattsController {

  private final PvWatts4Service v4Service;
  private final PvWatts5Service v5Service;
  
  @Inject
  public PvWattsController(PvWatts4Service v4Service, PvWatts5Service v5Service) {
    this.v4Service = v4Service;
    this.v5Service = v5Service;
  }
  
  @Path("v4.json")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public PvWatts4Response versionFourJsonResource(@Context UriInfo uriInfo) {
    return v4Service.execute(transformMap(uriInfo.getQueryParameters()));
  }
  
  @Path("v5.json")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public PvWatts5Response versionFiveJsonResource(@Context UriInfo uriInfo) {
    return v5Service.execute(transformMap(uriInfo.getQueryParameters()));
  }

  private static ImmutableMultimap<String, String> transformMap(
      MultivaluedMap<String, String> map) {
    ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
      builder.putAll(entry.getKey(), entry.getValue());
    }
    
    return builder.build();
  }
}
