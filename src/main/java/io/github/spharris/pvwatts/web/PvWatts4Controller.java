package io.github.spharris.pvwatts.web;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.github.spharris.pvwatts.service.v4.PvWatts4Service;

@Path("/")
public class PvWatts4Controller {

  private final PvWatts4Service service;
  
  @Inject
  public PvWatts4Controller(PvWatts4Service service) {
    this.service = service;
  }
  
  @Path("v4.xml")
  @GET
  public String versionFourXmlResource() {
    return "This is PVW4";
  }
  
  @Path("v4.json")
  @GET
  public String versionFourJsonResource() {
    return "This is PVW4";
  }
}
