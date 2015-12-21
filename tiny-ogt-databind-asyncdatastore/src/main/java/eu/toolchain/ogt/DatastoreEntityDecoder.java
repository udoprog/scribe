package eu.toolchain.ogt;

import com.spotify.asyncdatastoreclient.Entity;

import java.util.Optional;

import eu.toolchain.ogt.binding.FieldMapping;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatastoreEntityDecoder implements EntityDecoder {
    private final TypeEncodingProvider<byte[]> bytesEncoding;
    private final Entity entity;

    @Override
    public Optional<Object> decodeField(FieldMapping field, Context path) {
        return Optional.ofNullable(entity.getProperties().get(field.name()))
                .map(v -> field.type().decode(new DatastoreFieldDecoder(bytesEncoding, v), path));
    }

    @Override
    public Optional<String> decodeType() {
        return Optional.ofNullable(entity.getString("type"));
    }
}
