package eu.toolchain.scribe.datastore;

import com.google.datastore.v1.Value;
import eu.toolchain.scribe.EntityResolver;
import eu.toolchain.scribe.Option;
import eu.toolchain.scribe.TypeDecoderProvider;
import eu.toolchain.scribe.TypeEncoderProvider;
import eu.toolchain.scribe.TypeReference;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public class DatastoreEntityMapper {
  private final EntityResolver resolver;

  private final TypeEncoderProvider<Value> valueEncoder;
  private final TypeDecoderProvider<Value> valueDecoder;

  public DatastoreEntityMapper(final EntityResolver resolver) {
    this.resolver = resolver;

    final DatastoreEncodingFactory f = new DatastoreEncodingFactory(resolver);

    this.valueEncoder = resolver.encoderFor(f);
    this.valueDecoder = resolver.decoderFor(f);
  }

  public DatastoreEncoding<Object> encodingForType(final Type type) {
    return new DatastoreEncoding<>(valueEncoder.newEncoder(type), valueDecoder.newDecoder(type));
  }

  public <T> DatastoreEncoding<T> encodingFor(final Class<T> type) {
    return new DatastoreEncoding<>(valueEncoder.newEncoder(type), valueDecoder.newDecoder(type));
  }

  public <T> DatastoreEncoding<T> encodingFor(final TypeReference<T> type) {
    return new DatastoreEncoding<>(valueEncoder.newEncoder(type), valueDecoder.newDecoder(type));
  }

  public DatastoreEntityMapper withOptions(final Option... options) {
    return new DatastoreEntityMapper(resolver.withOptions(options));
  }
}
