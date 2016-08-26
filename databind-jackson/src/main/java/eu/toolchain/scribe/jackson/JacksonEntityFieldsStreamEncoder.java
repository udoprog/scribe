package eu.toolchain.scribe.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EntityFieldsStreamEncoder;
import eu.toolchain.scribe.entitymapping.EntityFieldStreamEncoder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class JacksonEntityFieldsStreamEncoder implements EntityFieldsStreamEncoder<JsonGenerator> {
  @Override
  public void encodeStart(final JsonGenerator generator) {
    try {
      generator.writeStartObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void encodeEnd(final JsonGenerator generator) {
    try {
      generator.writeEndObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <Source> void encodeField(
      final EntityFieldStreamEncoder<JsonGenerator, Source> field, final Context path,
      final Source value, final JsonGenerator generator
  ) {
    field.streamEncodeOptionally(path.push(field.getName()), value, generator, callable -> {
      try {
        generator.writeFieldName(field.getName());
      } catch (IOException e) {
        throw path.error(e);
      }

      callable.run();
    });
  }

  @Override
  public void encodeEmpty(final Context path, final JsonGenerator generator) {
    try {
      generator.writeNull();
    } catch (IOException e) {
      throw path.error(e);
    }
  }
}
