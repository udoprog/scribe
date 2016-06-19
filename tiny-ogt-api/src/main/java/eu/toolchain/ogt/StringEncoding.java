package eu.toolchain.ogt;

public interface StringEncoding<Source> {
    String encodeAsString(Source instance);

    Source decodeFromString(String source);
}
