package io.github.spharris.pvwatts.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.ImmutableMultimap;

public class ImmutableMultimapSerializer extends StdSerializer<ImmutableMultimap<String, String>> {

  private static final long serialVersionUID = 295125183211555478L;

  public ImmutableMultimapSerializer() {
    super(ImmutableMultimap.class, true);
  }

  @Override
  public void serialize(ImmutableMultimap<String, String> value,
      JsonGenerator gen,
      SerializerProvider provider) throws IOException {
    // TODO Auto-generated method stub
    
  }
}
