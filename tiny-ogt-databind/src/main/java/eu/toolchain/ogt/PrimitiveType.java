package eu.toolchain.ogt;

import java.io.IOException;
import java.util.Optional;

public enum PrimitiveType {
    SHORT(TypeDecoder::decodeShort, (e, v) -> e.encodeShort((short) v), "short"),
    INTEGER(TypeDecoder::decodeInteger, (e, v) -> e.encodeInteger((int) v), "int"),
    LONG(TypeDecoder::decodeLong, (e, v) -> e.encodeLong((long) v), "long"),
    FLOAT(TypeDecoder::decodeFloat, (e, v) -> e.encodeFloat((float) v), "float"),
    DOUBLE(TypeDecoder::decodeDouble, (e, v) -> e.encodeDouble((double) v), "double"),
    BOOLEAN(TypeDecoder::decodeBoolean, (e, v) -> e.encodeBoolean((boolean) v), "boolean"),
    BYTE(TypeDecoder::decodeByte, (e, v) -> e.encodeByte((byte) v), "byte"),
    CHAR(TypeDecoder::decodeCharacter, (e, v) -> e.encodeCharacter((char) v), "char");

    private final DecodingFunction decoding;
    private final EncodingFunction encoding;
    private final String name;

    PrimitiveType(
        final DecodingFunction accessor, final EncodingFunction setter, final String name
    ) {
        this.decoding = accessor;
        this.encoding = setter;
        this.name = name;
    }

    public static Optional<PrimitiveType> detect(final JavaType type) {
        final Class<?> raw = type.getRawClass();

        if (raw == boolean.class) {
            return Optional.of(BOOLEAN);
        }

        if (raw == byte.class) {
            return Optional.of(BYTE);
        }

        if (raw == char.class) {
            return Optional.of(CHAR);
        }

        if (raw == short.class) {
            return Optional.of(SHORT);
        }

        if (raw == int.class) {
            return Optional.of(INTEGER);
        }

        if (raw == long.class) {
            return Optional.of(LONG);
        }

        if (raw == float.class) {
            return Optional.of(FLOAT);
        }

        if (raw == double.class) {
            return Optional.of(DOUBLE);
        }

        return Optional.empty();
    }

    public <T> Object get(TypeDecoder<T> a, T instance) throws IOException {
        return decoding.decode(a, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T set(TypeEncoder<T> encoder, Object value) throws IOException {
        return (T) this.encoding.encode(encoder, value);
    }

    @Override
    public String toString() {
        return name;
    }

    public interface DecodingFunction {
        <T> Object decode(TypeDecoder<T> decoder, T instance) throws IOException;
    }

    public interface EncodingFunction {
        Object encode(TypeEncoder<?> encoder, Object value) throws IOException;
    }
}
