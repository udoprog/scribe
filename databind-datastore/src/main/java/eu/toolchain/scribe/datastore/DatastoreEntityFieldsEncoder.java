package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Value;
import eu.toolchain.scribe.Context;
import eu.toolchain.scribe.EntityFieldEncoder;
import eu.toolchain.scribe.EntityFieldsEncoder;
import eu.toolchain.scribe.Flags;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatastoreEntityFieldsEncoder implements EntityFieldsEncoder<Value, Entity> {
  final Entity.Builder object = Entity.newBuilder();

  @Override
  public <Source> void encodeField(
      EntityFieldEncoder<Value, Source> field, Context path, Source value
  ) {
    // TODO: implement infrastructure to make encoders aware of field annotations to avoid the
    // runtime and complexity penalty of checking them here.

    final Flags flags = field.getFlags();

    if (flags.getFlag(DatastoreFlags.KeyFlag.class).findFirst().isPresent()) {
      field.encodeOptionally(path.push(field.getName()), value, target -> {
        object.setKey(target.getKeyValue());
      });
    } else {
      field.encodeOptionally(path.push(field.getName()), value, target -> {
        object.getMutableProperties().put(field.getName(), target);
      });
    }
  }

  @Override
  public Entity buildEmpty(final Context path) {
    return Entity.newBuilder().build();
  }

  @Override
  public Entity build() {
    return object.build();
  }
}
