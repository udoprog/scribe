package eu.toolchain.ogt;

public class JacksonEncodingFactory implements EncodingFactory<JsonNode> {
    @Override
    public JacksonFieldEncoder fieldEncoder() {
        return new JacksonFieldEncoder();
    }

    @Override
    public JacksonFieldDecoder fieldDecoder() {
        return new JacksonFieldDecoder();
    }
}
