package eu.toolchain.ogt.creatormethod;

import eu.toolchain.ogt.EntityField;

import java.util.List;

public interface CreatorMethod {
    List<EntityField> fields();

    InstanceBuilder instanceBuilder();
}
