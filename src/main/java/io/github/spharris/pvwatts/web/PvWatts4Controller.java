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
import com.google.common.collect.Iterables;

import io.github.spharris.pvwatts.service.v4.PvWatts4Request;
import io.github.spharris.pvwatts.service.v4.PvWatts4Response;
import io.github.spharris.pvwatts.service.v4.PvWatts4Service;

@Path("/")
final class PvWatts4Controller {

  private final PvWatts4Service service;
  
  @Inject
  public PvWatts4Controller(PvWatts4Service service) {
    this.service = service;
  }
  
  @Path("v4.xml")
  @GET
  @Produces(MediaType.APPLICATION_XML)
  public PvWatts4Response  versionFourXmlResource(@Context UriInfo uriInfo) {
    return service.execute(buildRequest(uriInfo.getQueryParameters()));
  }
  
  @Path("v4.json")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public PvWatts4Response versionFourJsonResource(@Context UriInfo uriInfo) {
    return service.execute(buildRequest(uriInfo.getQueryParameters()));
  }
  
  private static PvWatts4Request buildRequest(
      MultivaluedMap<String, String> map) {
    
    ImmutableMultimap<String, String> multimap = transformMap(map);
    return PvWatts4Request.builder()
      .setSystemSize(parseFloat(Iterables.getFirst(multimap.get("system_size"), null)))
      .setAddress(Iterables.getFirst(multimap.get("address"), null))
      .setLat(parseFloat(Iterables.getFirst(multimap.get("lat"), null)))
      .setLon(parseFloat(Iterables.getFirst(multimap.get("lon"), null)))
      .setFileId(Iterables.getFirst(multimap.get("file_id"), null))
      .setDataset(Iterables.getFirst(multimap.get("dataset"), null))
      .setRadius(parseInt(Iterables.getFirst(multimap.get("radius"), "100")))
      .setTimeframe(Iterables.getFirst(multimap.get("timeframe"), "monthly"))
      .setAzimuth(parseFloat(Iterables.getFirst(multimap.get("azimuth"), null)))
      .setDerate(parseFloat(Iterables.getFirst(multimap.get("derate"), null)))
      .setTilt(parseFloat(Iterables.getFirst(multimap.get("tilt"), null)))
      .setTiltEqLat(parseInt(Iterables.getFirst(multimap.get("tilt_eq_lat"), "0")))
      .setTrackMode(parseInt(Iterables.getFirst(multimap.get("track_mode"), "1")))
      .setInoct(parseFloat(Iterables.getFirst(multimap.get("inoct"), null)))
      .setGamma(parseFloat(Iterables.getFirst(multimap.get("gamma"), null)))
      .setCallback(Iterables.getFirst(multimap.get("callback"), null))
      .build();
  }
  
  private static ImmutableMultimap<String, String> transformMap(
      MultivaluedMap<String, String> map) {
    ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
      builder.putAll(entry.getKey(), entry.getValue());
    }
    
    return builder.build();
  }
  
  private static Integer parseInt(String data) {
    try {
      return Integer.valueOf(data);
    } catch (Exception e) {
      return null;
    }
  }
  
  private static Float parseFloat(String data) {
    try {
      return Float.valueOf(data);
    } catch (Exception e) {
      return null;
    }
  }
}
