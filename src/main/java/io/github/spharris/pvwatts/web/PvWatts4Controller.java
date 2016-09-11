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

import io.github.spharris.pvwatts.service.v4.PvWatts4Response;
import io.github.spharris.pvwatts.service.v4.PvWatts4Service;

@Path("/")
public final class PvWatts4Controller {

  private final PvWatts4Service service;
  
  @Inject
  public PvWatts4Controller(PvWatts4Service service) {
    this.service = service;
  }
  
  @Path("v4.xml")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  public PvWatts4Response  versionFourXmlResource(@Context UriInfo uriInfo) {
    return service.execute(transformMap(uriInfo.getQueryParameters()));
  }
  
  @Path("v4.json")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public PvWatts4Response versionFourJsonResource(@Context UriInfo uriInfo) {
    return service.execute(transformMap(uriInfo.getQueryParameters()));
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
