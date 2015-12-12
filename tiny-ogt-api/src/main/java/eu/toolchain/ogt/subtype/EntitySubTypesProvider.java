package eu.toolchain.ogt.subtype;

import java.util.Map;

import eu.toolchain.ogt.type.EntityTypeMapping;

public interface EntitySubTypesProvider {
    Map<String, EntityTypeMapping> subtypes();
}
