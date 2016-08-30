package eu.toolchain.scribe;

public interface EntityFieldDecoder<Target, Source> extends EntityFieldDescriptor {
  String getName();

  Decoded<Source> decode(Context path, Target instance);

  Decoded<Source> decodeOptionally(Context path, Decoded<Target> instance);
}
