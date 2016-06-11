package eu.toolchain.ogt;

import java.io.IOException;
import java.util.Optional;

public enum BoxedPrimitiveType {
    SHORT(TypeDecoder::decodeShort, (e, v) -> e.encodeShort((short) v), "Short"),
    INTEGER(TypeDecoder::decodeInteger, (e, v) -> e.encodeInteger((int) v), "Integer"),
    LONG(TypeDecoder::decodeLong, (e, v) -> e.encodeLong((long) v), "Long"),
    FLOAT(TypeDecoder::decodeFloat, (e, v) -> e.encodeFloat((float) v), "Float"),
    DOUBLE(TypeDecoder::decodeDouble, (e, v) -> e.encodeDouble((double) v), "Double"),
    BOOLEAN(TypeDecoder::decodeBoolean, (e, v) -> e.encodeBoolean((boolean) v), "Boolean"),
    BYTE(TypeDecoder::decodeByte, (e, v) -> e.encodeByte((byte) v), "Byte"),
    CHAR(TypeDecoder::decodeCharacter, (e, v) -> e.encodeCharacter((char) v), "Char");

    private final DecodingFunction decoding;
    private final EncodingFunction encoding;
    private final String name;

    BoxedPrimitiveType(
        final DecodingFunction accessor, final EncodingFunction setter, final String name
    ) {
        this.decoding = accessor;
        this.encoding = setter;
        this.name = name;
    }

    public static Optional<BoxedPrimitiveType> detect(final JavaType type) {
        final Class<?> raw = type.getRawClass();

        if (raw == Boolean.class) {
            return Optional.of(BOOLEAN);
        }

        if (raw == Byte.class) {
            return Optional.of(BYTE);
        }

        if (raw == Character.class) {
            return Optional.of(CHAR);
        }

        if (raw == Short.class) {
            return Optional.of(SHORT);
        }

        if (raw == Integer.class) {
            return Optional.of(INTEGER);
        }

        if (raw == Long.class) {
            return Optional.of(LONG);
        }

        if (raw == Float.class) {
            return Optional.of(FLOAT);
        }

        if (raw == Double.class) {
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
