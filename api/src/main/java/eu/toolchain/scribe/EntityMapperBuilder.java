package eu.toolchain.scribe;

import eu.toolchain.scribe.entitymapper.CreatorMethodDetector;
import eu.toolchain.scribe.entitymapper.DecodeValueDetector;
import eu.toolchain.scribe.entitymapper.EncodeValueDetector;
import eu.toolchain.scribe.entitymapper.EntityMappingDetector;
import eu.toolchain.scribe.entitymapper.FieldFlagDetector;
import eu.toolchain.scribe.entitymapper.FieldNameDetector;
import eu.toolchain.scribe.entitymapper.FieldReaderDetector;
import eu.toolchain.scribe.entitymapper.SubTypesDetector;
import eu.toolchain.scribe.entitymapper.TypeAliasDetector;
import eu.toolchain.scribe.entitymapper.TypeNameDetector;
import eu.toolchain.scribe.typemapper.TypeMapper;

public interface EntityMapperBuilder<T> {
  EntityMapperBuilder<T> typeAliasDetector(TypeAliasDetector typeAliasDetector);

  EntityMapperBuilder<T> typeMapper(TypeMapper typeMapper);

  EntityMapperBuilder<T> fieldReaderDetector(FieldReaderDetector fieldReader);

  EntityMapperBuilder<T> creatorMethodDetector(CreatorMethodDetector creatorMethod);

  EntityMapperBuilder<T> entityMappingDetector(EntityMappingDetector binding);

  EntityMapperBuilder<T> subTypesDetector(SubTypesDetector subTypeDetector);

  EntityMapperBuilder<T> encodeValueDetector(EncodeValueDetector encodeValueDetector);

  EntityMapperBuilder<T> decodeValueDetector(DecodeValueDetector encodeValueDetector);

  EntityMapperBuilder<T> fieldNameDetector(FieldNameDetector fieldNameDetector);

  EntityMapperBuilder<T> fieldFlagDetector(FieldFlagDetector fieldFlagDetector);

  EntityMapperBuilder<T> typeNameDetector(TypeNameDetector typeNameDetector);

  EntityMapperBuilder<T> register(Module module);

  EntityMapperBuilder<T> option(Option option);

  T build();
}
