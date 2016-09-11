package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.Annotations;
import eu.toolchain.scribe.reflection.JavaType;

import java.util.List;
import java.util.Optional;

/**
 * Primary interface for mapping information about entities.
 */
public interface EntityResolver {
  /**
   * Create a {@link Mapping} out of a class.
   *
   * @param cls Class to create {@link Mapping} from.
   * @return A {@link Mapping} created from the given class.
   */
  @SuppressWarnings("unchecked")
  default <T> Mapping<T> mapping(Class<T> cls) {
    return (Mapping<T>) mapping(JavaType.of(cls));
  }

  /**
   * Performed a cached lookup of the given type.
   * <p>
   * This will put the resolved type into cache before they are being initialized to allow for
   * circular dependencies.
   *
   * @param type The type to lookup.
   * @return A mapping for the given type.
   */
  Mapping<Object> mapping(JavaType type);

  /**
   * Performed a cached lookup of the given type.
   * <p>
   * This will put the resolved type into cache before they are being initialized to allow for
   * circular dependencies.
   *
   * @param type The type to lookup.
   * @param annotations External annotations associated with the given type.
   * @return A mapping for the given type.
   */
  Mapping<Object> mapping(JavaType type, Annotations annotations);

  /**
   * Create an encoder provider for the given factory.
   *
   * @param factory Encoder factory to create provider for.
   * @param <Target> Target type of the provider.
   * @return A type encoder provider.
   */
  <Target, EntityTarget> TypeEncoderProvider<Target> encoderFor(
      final EncoderFactory<Target, EntityTarget> factory
  );

  /**
   * Create a stream encoder provider for the given factory.
   *
   * @param factory Stream encoder factory to create provider for.
   * @param <Target> Target type of the provider.
   * @return A type stream encoder provider.
   */
  <Target> TypeStreamEncoderProvider<Target> streamEncoderFor(
      final StreamEncoderFactory<Target> factory
  );

  /**
   * Create a decoder provider for the given factory.
   *
   * @param factory Decoder factory to create provider for.
   * @param <Target> Target type of the provider.
   * @return A type decoder provider.
   */
  <Target, EntityTarget> TypeDecoderProvider<Target> decoderFor(
      final DecoderFactory<Target, EntityTarget> factory
  );

  /**
   * Detect a method of creating instances of the given type.
   *
   * @param type Type to create instances of.
   * @return An optional {@link InstanceBuilder}.
   */
  Optional<InstanceBuilder<Object>> detectInstanceBuilder(JavaType type);

  /**
   * Detect how to read the given field.
   *
   * @param type The type the field is associated with.
   * @param fieldName The name of the field.
   * @param fieldType The type of the field.
   * @return The field reader for the given type.
   */
  Optional<FieldReader> detectFieldReader(
      JavaType type, String fieldName, JavaType fieldType
  );

  /**
   * Detect the mapping for an entity.
   * <p>
   * An entity mapping is a method for creating an entity and its associated fields.
   *
   * @param type Entity type to detect mapping for.
   * @return An optional entity mapping for the given type.
   */
  Optional<ClassEncoding<Object>> detectEntityMapping(JavaType type);

  /**
   * Detect encode-value methods for the given type.
   * <p>
   * Encode-value methods are ways that the entity should be converted into another type when it's
   * encoded, like if a method is annotated so that it should return the value of the current
   * entity
   * as a string.
   *
   * @param type Type to detect encode-value methods for.
   * @return An optional encode-value method for the given type.
   */
  Optional<EncodeValue<Object>> detectEncodeValue(JavaType type);

  /**
   * Detect decode-value methods for the given type. Similar to {@link
   * #detectEncodeValue(JavaType)}, but for mapping an external value into the given entity.
   *
   * @param type Type to detect decode-value methods for.
   * @return An optional decode-value method for the given type.
   * @see #detectEncodeValue(JavaType)
   */
  Optional<DecodeValue<Object>> detectDecodeValue(JavaType type, JavaType fieldType);

  /**
   * Detect the field name of the given type, with the given set of annotations.
   *
   * @param type Type to detect field name for.
   * @param annotations Annotations to detect field name for.
   * @return An optional field name.
   */
  Optional<String> detectFieldName(JavaType type, Annotations annotations);

  /**
   * Detect the name of the given type.
   * <p>
   * Type names are used to map sub-types of a given abstract class. They are typically either
   * mapped immediately on the sub-class or in the abstract class itself through annotations.
   *
   * @param type Type to detect a name for.
   * @return An optional type name.
   */
  Optional<String> detectTypeName(JavaType type);

  /**
   * Get a list of entity fields for the given executable type.
   *
   * @param executable Executable type to get fields for.
   * @return A list of fields associated with the executable type.
   */
  List<EntityField> detectExecutableFields(ExecutableType executable);

  /**
   * Detect all available field flags.
   *
   * @param type Type of the field.
   * @param annotations Annotations associated with the field.
   * @return The detected flags for the given type.
   */
  Flags detectFieldFlags(JavaType type, Annotations annotations);

  /**
   * Get a value for the type of option, if present.
   *
   * @param option Class of option to get.
   * @param <O> Type of option to get.
   * @return A value or empty if absent.
   */
  <O extends Option> Optional<O> getOption(Class<O> option);

  /**
   * Check if the given option is present.
   *
   * @param option Option to check the presence for.
   * @return {@code true} if the option is present.
   */
  <O extends Option> boolean isOptionPresent(O option);

  /**
   * Create a new resolver with the given options added to it.
   * <p>
   * Note: This operation invalidates entity caches for the new resolver.
   *
   * @param options Options to add to resolver.
   * @return A new resolver with the new options added to it.
   */
  EntityResolver withOptions(Option... options);

  /**
   * Detect immediate annotations for the given field.
   *
   * @param type Type to detect immediate annotations on its fields.
   * @param fieldName name of field to look for.
   * @return Annotations associated with the immediate field.
   */
  Annotations detectImmediateAnnotations(JavaType type, String fieldName);
}
