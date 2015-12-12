package eu.toolchain.ogt.creatormethod;

import java.lang.reflect.Parameter;

import eu.toolchain.ogt.JavaType;
import eu.toolchain.ogt.type.TypeMapping;

public interface CreatorField {
    boolean indexed();

    JavaType type();

    TypeMapping mapping();

    Parameter parameter();
}
