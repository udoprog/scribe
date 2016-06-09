package eu.toolchain.ogt;

public class JacksonEncodingFactory implements EncodingFactory<JsonNode> {
    @Override
    public JacksonTypeEncoder fieldEncoder() {
        return new JacksonTypeEncoder();
    }

    @Override
    public JacksonTypeDecoder fieldDecoder() {
        return new JacksonTypeDecoder();
    }
}
