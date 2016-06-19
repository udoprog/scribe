package eu.toolchain.ogt.creatormethod;

import java.util.List;

public interface InstanceBuilder {
    Object newInstance(List<Object> arguments);
}
