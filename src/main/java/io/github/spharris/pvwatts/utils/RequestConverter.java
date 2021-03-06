package io.github.spharris.pvwatts.utils;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import io.github.spharris.pvwatts.service.PvWatts4Request;
import io.github.spharris.pvwatts.service.PvWatts5Request;

public final class RequestConverter {

  private RequestConverter() {}

  public static PvWatts4Request toPvWatts4Request(ImmutableMultimap<String, String> multimap) {
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

  public static PvWatts5Request toPvWatts5Request(ImmutableMultimap<String, String> multimap) {
    return PvWatts5Request.builder()
        .setSystemCapacity(parseFloat(Iterables.getFirst(multimap.get("system_capacity"), null)))
        .setModuleType(parseInt(Iterables.getFirst(multimap.get("module_type"), null)))
        .setLosses(parseFloat(Iterables.getFirst(multimap.get("losses"), null)))
        .setArrayType(parseInt(Iterables.getFirst(multimap.get("array_type"), null)))
        .setTilt(parseFloat(Iterables.getFirst(multimap.get("tilt"), null)))
        .setAzimuth(parseFloat(Iterables.getFirst(multimap.get("azimuth"), null)))
        .setAddress(Iterables.getFirst(multimap.get("address"), null))
        .setLat(parseFloat(Iterables.getFirst(multimap.get("lat"), null)))
        .setLon(parseFloat(Iterables.getFirst(multimap.get("lon"), null)))
        .setFileId(Iterables.getFirst(multimap.get("file_id"), null))
        .setDataset(Iterables.getFirst(multimap.get("dataset"), null))
        .setRadius(parseInt(Iterables.getFirst(multimap.get("radius"), "100")))
        .setTimeframe(Iterables.getFirst(multimap.get("timeframe"), "monthly"))
        .setDcAcRatio(parseFloat(Iterables.getFirst(multimap.get("dc_ac_ratio"), "1.1")))
        .setGcr(parseFloat(Iterables.getFirst(multimap.get("gcr"), "0.4")))
        .setInvEff(parseFloat(Iterables.getFirst(multimap.get("inv_eff"), null)))
        .setCallback(Iterables.getFirst(multimap.get("callback"), null))
        .build();
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
