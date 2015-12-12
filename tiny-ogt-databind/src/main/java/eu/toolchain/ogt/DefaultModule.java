package eu.toolchain.ogt;

import eu.toolchain.ogt.binding.BuilderBinding;
import eu.toolchain.ogt.binding.ConstructorBinding;
import eu.toolchain.ogt.fieldreader.GetterFieldReader;

public class DefaultModule implements Module {
    @Override
    public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> builder) {
        return builder.registerFieldReader(GetterFieldReader::detect)
                .registerBinding(ConstructorBinding::detect)
                .registerBinding(BuilderBinding::detect);
    }
}
