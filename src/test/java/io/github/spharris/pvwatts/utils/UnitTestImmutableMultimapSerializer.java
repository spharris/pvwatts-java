package io.github.spharris.pvwatts.utils;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMultimap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UnitTestImmutableMultimapSerializer {

  private ObjectMapper mapper = new ObjectMapper();

  @Test
  public void emptyMapIsEmpty() throws Exception {
    ImmutableMultimap<String, String> map = ImmutableMultimap.of();

    TestClass data = new TestClass();
    data.setData(map);
    String expected = "{\"data\":{}}";

    assertThat(mapper.writeValueAsString(data)).isEqualTo(expected);
  }

  @Test
  public void singleItemIsRawType() throws Exception {
    ImmutableMultimap<String, String> map = ImmutableMultimap.of("key", "value");

    TestClass data = new TestClass();
    data.setData(map);
    String expected = "{\"data\":{\"key\":\"value\"}}";

    assertThat(mapper.writeValueAsString(data)).isEqualTo(expected);
  }

  @Test
  public void multipleItemsIsList() throws Exception {
    ImmutableMultimap<String, String> map =
        ImmutableMultimap.<String, String>builder().putAll("key", "v1", "v2", "v3").build();

    TestClass data = new TestClass();
    data.setData(map);
    String expected = "{\"data\":{\"key\":[\"v1\",\"v2\",\"v3\"]}}";

    assertThat(mapper.writeValueAsString(data)).isEqualTo(expected);
  }

  static class TestClass {

    ImmutableMultimap<String, String> data;

    public void setData(ImmutableMultimap<String, String> data) {
      this.data = data;
    }

    @JsonSerialize(using = ImmutableMultimapSerializer.class)
    public ImmutableMultimap<String, String> getData() {
      return data;
    }
  }
}
