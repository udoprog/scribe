package eu.toolchain.scribe;

import eu.toolchain.scribe.detector.ClassEncodingDetector;
import eu.toolchain.scribe.detector.DecodeValueDetector;
import eu.toolchain.scribe.detector.EncodeValueDetector;
import eu.toolchain.scribe.detector.FieldNameDetector;
import eu.toolchain.scribe.detector.FieldReaderDetector;
import eu.toolchain.scribe.detector.FlagDetector;
import eu.toolchain.scribe.detector.InstanceBuilderDetector;
import eu.toolchain.scribe.detector.MappingDetector;
import eu.toolchain.scribe.detector.SubTypesDetector;
import eu.toolchain.scribe.detector.TypeAliasDetector;
import eu.toolchain.scribe.detector.TypeNameDetector;

public interface EntityMapperBuilder {
  /**
   * Install a type alias detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder typeAlias(TypeAliasDetector detector);

  /**
   * Install a mapping detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder mapping(MappingDetector detector);

  /**
   * Install a field reader detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder fieldReader(FieldReaderDetector detector);

  /**
   * Install an instance builder detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder instanceBuilder(InstanceBuilderDetector detector);

  /**
   * Install an class encoding detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder classEncoding(ClassEncodingDetector detector);

  /**
   * Install an sub types detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder subTypes(SubTypesDetector detector);

  /**
   * Install an encode value detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder encodeValue(EncodeValueDetector detector);

  /**
   * Install a decode value detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder decodeValue(DecodeValueDetector detector);

  /**
   * Install a field name detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder fieldName(FieldNameDetector detector);

  /**
   * Install a flag detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder flag(FlagDetector detector);

  /**
   * Install a type name detector.
   *
   * @param detector Detector to install.
   * @return This builder.
   */
  EntityMapperBuilder typeName(TypeNameDetector detector);

  /**
   * Install a module.
   * <p>
   * A module is a set of commands to run against this builder.
   *
   * @param module Module to install.
   * @return This builder.
   */
  EntityMapperBuilder install(Module module);

  /**
   * Set an option.
   *
   * @param option Option to set.
   * @return This builder.
   * @see eu.toolchain.scribe.EntityResolver#getOption(Class)
   */
  EntityMapperBuilder option(Option option);
}
