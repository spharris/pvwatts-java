package io.github.spharris.pvwatts.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class PvWatts5Controller {

  @Path("v5.xml")
  @GET
  public String versionFiveXmlResource() {
    return "This is PVW5";
  }
  
  @Path("v5.json")
  @GET
  public String versionFiveJsonResource() {
    return "This is PVW5";
  }
}
