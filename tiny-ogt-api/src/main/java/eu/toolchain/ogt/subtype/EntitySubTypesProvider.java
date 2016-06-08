package eu.toolchain.ogt.subtype;

import eu.toolchain.ogt.type.EntityTypeMapping;

import java.util.Map;

public interface EntitySubTypesProvider {
    Map<String, EntityTypeMapping> subtypes();
}
