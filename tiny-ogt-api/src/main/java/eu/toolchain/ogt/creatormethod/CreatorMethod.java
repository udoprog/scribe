package eu.toolchain.ogt.creatormethod;

import java.util.List;

public interface CreatorMethod {
    List<CreatorField> fields();

    InstanceBuilder instanceBuilder();
}
