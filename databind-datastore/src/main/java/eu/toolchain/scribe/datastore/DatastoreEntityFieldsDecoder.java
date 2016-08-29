package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.EntityFieldsDecoder;
import eu.toolchain.scribe.entitymapping.EntityFieldDecoder;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatastoreEntityFieldsDecoder implements EntityFieldsDecoder<Value> {
  private final Map<String, Value> value;

  @Override
  public <Source> Decoded<Source> decodeField(
      final EntityFieldDecoder<Value, Source> decoder, final Context path
  ) {
    return decoder.decodeOptionally(path, Decoded.ofNullable(value.get(decoder.getName())));
  }
}
