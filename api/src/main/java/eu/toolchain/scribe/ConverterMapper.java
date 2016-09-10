package eu.toolchain.scribe;

import java.lang.reflect.Type;

public interface ConverterMapper<Value> {
  ConverterEncoding<Object, Value> valueEncodingForType(final Type type);

  @SuppressWarnings("unchecked")
  default <T> ConverterEncoding<T, Value> valueEncodingFor(final Class<T> type) {
    return (ConverterEncoding<T, Value>) valueEncodingForType(type);
  }

  @SuppressWarnings("unchecked")
  default <T> ConverterEncoding<T, Value> valueEncodingFor(final TypeReference<T> type) {
    return (ConverterEncoding<T, Value>) valueEncodingForType(type.getType());
  }
}
