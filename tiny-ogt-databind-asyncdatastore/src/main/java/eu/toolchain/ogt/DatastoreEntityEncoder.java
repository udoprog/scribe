package eu.toolchain.ogt;

import com.spotify.asyncdatastoreclient.Entity;

import java.io.IOException;

import eu.toolchain.ogt.binding.FieldMapping;

public class DatastoreEntityEncoder implements EntityEncoder {
    private final Entity.Builder builder;

    public DatastoreEntityEncoder(final Entity.Builder builder) {
        this.builder = builder;
    }

    @Override
    public void setType(String type) throws IOException {
        builder.property("type", type);
    }

    @Override
    public FieldEncoder setField(FieldMapping field) throws IOException {
        return new DatastoreFieldEncoder(v -> builder.property(field.name(), v),
                values -> builder.property(field.name(), values));
    }
}
