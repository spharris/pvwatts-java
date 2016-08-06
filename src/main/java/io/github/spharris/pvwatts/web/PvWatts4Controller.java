package io.github.spharris.pvwatts.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class PvWatts4Controller {

  @Inject
  public PvWatts4Controllre
  
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
