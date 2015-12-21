package eu.toolchain.ogt;

import com.spotify.asyncdatastoreclient.Entity;
import com.spotify.asyncdatastoreclient.Value;

public class DatastoreEncodingFactory implements EncodingFactory<Entity> {
    private final TypeEncodingProvider<byte[]> bytesEncoding;

    public DatastoreEncodingFactory(final TypeEncodingProvider<byte[]> bytesEncoding) {
        this.bytesEncoding = bytesEncoding;
    }

    @Override
    public EntityEncoder entityEncoder() {
        return new DatastoreEntityEncoder(bytesEncoding);
    }

    @Override
    public EntityDecoder entityDecoder(final Entity entity) {
        return new DatastoreEntityDecoder(bytesEncoding, entity);
    }

    @Override
    public FieldEncoder fieldEncoder() {
        return new DatastoreFieldEncoder(bytesEncoding);
    }

    @Override
    public FieldDecoder fieldDecoder(Entity input) {
        return new DatastoreFieldDecoder(bytesEncoding,
                Value.builder(input).build());
    }
}
