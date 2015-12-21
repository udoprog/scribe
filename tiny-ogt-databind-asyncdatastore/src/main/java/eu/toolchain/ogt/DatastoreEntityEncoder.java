package eu.toolchain.ogt;

import com.spotify.asyncdatastoreclient.Entity;

import java.io.IOException;
import java.util.List;

import eu.toolchain.ogt.binding.FieldMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatastoreEntityEncoder implements EntityEncoder {
    private final TypeEncodingProvider<byte[]> bytesEncoding;
    private final Entity.Builder builder = Entity.builder();

    @Override
    public void setType(String type) throws IOException {
        builder.property("type", type);
    }

    @Override
    public void setField(FieldMapping field, Context path, Object value) throws IOException {
        final Object v = field.type().encode(new DatastoreFieldEncoder(bytesEncoding), path, value);

        if (v instanceof List) {
            builder.property(field.name(), (List<Object>) v);
        } else {
            builder.property(field.name(), v);
        }
    }

    @Override
    public Object encode() {
        return builder.build();
    }
}
