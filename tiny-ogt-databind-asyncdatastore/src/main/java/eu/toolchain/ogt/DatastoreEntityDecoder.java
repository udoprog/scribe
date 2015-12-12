package eu.toolchain.ogt;

import com.spotify.asyncdatastoreclient.Entity;

import java.util.Optional;

import eu.toolchain.ogt.binding.FieldMapping;

public class DatastoreEntityDecoder implements EntityDecoder {
    private final Entity entity;

    public DatastoreEntityDecoder(final Entity entity) {
        this.entity = entity;
    }

    @Override
    public Optional<FieldDecoder> getField(FieldMapping field) {
        return Optional.ofNullable(entity.getProperties().get(field.name()))
                .map(DatastoreFieldDecoder::new);
    }

    @Override
    public Optional<String> getType() {
        return Optional.ofNullable(entity.getString("type"));
    }
}
