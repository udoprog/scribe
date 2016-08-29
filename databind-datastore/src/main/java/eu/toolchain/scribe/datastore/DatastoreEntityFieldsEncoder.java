package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Value;
import com.google.protobuf.NullValue;

import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EntityFieldsEncoder;
import eu.toolchain.scribe.entitymapping.EntityFieldEncoder;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatastoreEntityFieldsEncoder implements EntityFieldsEncoder<Value> {
  final Map<String, Value> object = new HashMap<>();

  @Override
  public <Source> void encodeField(
      EntityFieldEncoder<Value, Source> field, Context path, Source value
  ) {
    field.encodeOptionally(path.push(field.getName()), value,
        target -> object.put(field.getName(), target));
  }

  @Override
  public Value buildEmpty(final Context path) {
    return Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build();
  }

  @Override
  public Value build() {
    final Entity entity = Entity.newBuilder().putAllProperties(object).build();
    return Value.newBuilder().setEntityValue(entity).build();
  }
}
