package eu.toolchain.ogt;

import com.spotify.asyncdatastoreclient.Entity;

import java.io.IOException;

import eu.toolchain.ogt.binding.FieldMapping;

public class DatastoreEncodingFactory implements EncodingFactory<Entity> {
    public DatastoreEncodingFactory() {
    }

    @Override
    public BuildableEntityEncoder<Entity> entityEncoder() {
        final Entity.Builder builder = Entity.builder();
        final EntityEncoder encoder = new DatastoreEntityEncoder(builder);

        return new BuildableEntityEncoder<Entity>() {
            @Override
            public void setType(String type) throws IOException {
                encoder.setType(type);
            }

            @Override
            public FieldEncoder setField(FieldMapping field) throws IOException {
                return encoder.setField(field);
            }

            @Override
            public Entity build() {
                return builder.build();
            }
        };
    }

    @Override
    public EntityDecoder entityDecoder(final Entity entity) {
        return new DatastoreEntityDecoder(entity);
    }
}
