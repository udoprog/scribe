package eu.toolchain.scribe.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EntityFieldStreamEncoder;
import eu.toolchain.scribe.EntityFieldsStreamEncoder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class JacksonEntityFieldsStreamEncoder implements EntityFieldsStreamEncoder<JsonGenerator> {
  @Override
  public void encodeStart(final Context path, final JsonGenerator generator) {
    try {
      generator.writeStartObject();
    } catch (final Exception e) {
      throw path.error("failed to write start of object", e);
    }
  }

  @Override
  public void encodeEnd(final Context path, final JsonGenerator generator) {
    try {
      generator.writeEndObject();
    } catch (final Exception e) {
      throw path.error("failed to write end of object", e);
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
