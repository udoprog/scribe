package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Value;

import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.TypeDecoderProvider;
import eu.toolchain.scribe.TypeEncoderProvider;
import eu.toolchain.scribe.TypeReference;

import java.lang.reflect.Type;

import lombok.Data;

@Data
public class DatastoreEntityMapper {
  private final EntityResolver resolver;

  private final TypeEncoderProvider<Value> encoderProvider;
  private final TypeDecoderProvider<Value> decoderProvider;

  public DatastoreEntityMapper(final EntityResolver resolver) {
    this.resolver = resolver;

    final DatastoreEncodingFactory f = new DatastoreEncodingFactory(resolver);

    this.encoderProvider = resolver.encoderFor(f);
    this.decoderProvider = resolver.decoderFor(f);
  }

  public DatastoreEncoding<Object> encodingForType(final Type type) {
    return new DatastoreEncoding<>(encoderProvider.newEncoder(type),
        decoderProvider.newDecoder(type));
  }

  public <T> DatastoreEncoding<T> encodingFor(final Class<T> type) {
    return new DatastoreEncoding<>(encoderProvider.newEncoder(type),
        decoderProvider.newDecoder(type));
  }

  public <T> DatastoreEncoding<T> encodingFor(final TypeReference<T> type) {
    return new DatastoreEncoding<>(encoderProvider.newEncoder(type),
        decoderProvider.newDecoder(type));
  }

  public DatastoreEntityMapper withOptions(final Option... options) {
    return new DatastoreEntityMapper(resolver.withOptions(options));
  }
}
