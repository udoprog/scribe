package eu.toolchain.scribe;

import java.lang.reflect.Type;

public interface EntityConverterMapper<EntityValue> {
  ConverterEncoding<Object, EntityValue> entityEncodingForType(final Type type);

  @SuppressWarnings("unchecked")
  default <T> ConverterEncoding<T, EntityValue> entityEncodingFor(final TypeReference<T> type) {
    return (ConverterEncoding<T, EntityValue>) entityEncodingForType(type.getType());
  }

  @SuppressWarnings("unchecked")
  default <T> ConverterEncoding<T, EntityValue> entityEncodingFor(final Class<T> type) {
    return (ConverterEncoding<T, EntityValue>) entityEncodingForType(type);
  }
}
