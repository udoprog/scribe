package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Value;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.Decoded;
import eu.toolchain.scribe.EntityFieldsDecoder;
import eu.toolchain.scribe.Flags;
import eu.toolchain.scribe.entitymapping.EntityFieldDecoder;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DatastoreEntityFieldsDecoder implements EntityFieldsDecoder<Value> {
  private final Supplier<Decoded<Value>> key;
  private final Map<String, Value> value;

  @Override
  public <Source> Decoded<Source> decodeField(
      final EntityFieldDecoder<Value, Source> field, final Context path
  ) {
    final Flags flags = field.getFlags();

    if (flags.getFlag(DatastoreFlags.KeyFlag.class).findFirst().isPresent()) {
      return field.decodeOptionally(path, key.get());
    } else {
      return field.decodeOptionally(path, Decoded.ofNullable(value.get(field.getName())));
    }
  }
}
