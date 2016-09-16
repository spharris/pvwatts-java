package io.github.spharris.pvwatts.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;

public final class ImmutableMultimapSerializer
    extends JsonSerializer<ImmutableMultimap<String, String>> {
  
  @Override
  public void serialize(ImmutableMultimap<String, String> value,
      JsonGenerator gen,
      SerializerProvider provider) throws IOException {
    gen.writeStartObject();
    
    for (Entry<String, Collection<String>> entry : value.asMap().entrySet()) {
      Collection<String> values = entry.getValue();
      if (values.isEmpty()) {
        continue;
      }
      
      gen.writeFieldName(entry.getKey());
      
      if (values.size() == 1) {
        gen.writeObject(Iterables.getOnlyElement(values));
      } else {
        gen.writeObject(values);
      }
    }
    
    gen.writeEndObject();
  }
}
