package eu.toolchain.scribe;

import java.util.function.Consumer;

public interface EntityFieldEncoder<Target, Source> extends EntityFieldDescriptor {
  String getName();

  Target encode(Context path, Source instance);

  void encodeOptionally(Context path, Source instance, Consumer<Target> consumer);
}
