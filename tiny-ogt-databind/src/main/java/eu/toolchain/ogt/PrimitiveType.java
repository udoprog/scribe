package eu.toolchain.ogt;

import java.io.IOException;
import java.util.Optional;

public enum PrimitiveType {
    SHORT(FieldDecoder::asShort, (e, v) -> e.setShort((short) v)), INTEGER(FieldDecoder::asInteger,
            (e, v) -> e.setInteger((int) v)), LONG(FieldDecoder::asLong,
                    (e, v) -> e.setLong((long) v)), FLOAT(FieldDecoder::asFloat,
                            (e, v) -> e.setFloat((float) v)), DOUBLE(FieldDecoder::asDouble,
                                    (e, v) -> e.setDouble((double) v)), BOOLEAN(
                                            FieldDecoder::asBoolean,
                                            (e, v) -> e.setBoolean((boolean) v)), BYTE(
                                                    FieldDecoder::asByte,
                                                    (e, v) -> e.setByte((byte) v)), CHAR(
                                                            FieldDecoder::asCharacter,
                                                            (e, v) -> e.setCharacter((char) v));

    private final DecodingFunction decoding;
    private final EncodingFunction encoding;

    private PrimitiveType(final DecodingFunction accessor, final EncodingFunction setter) {
        this.decoding = accessor;
        this.encoding = setter;
    }

    public static Optional<PrimitiveType> detect(final JavaType type) {
        final Class<?> raw = type.getRawClass();

        if (raw == Boolean.class || raw == boolean.class) {
            return Optional.of(BOOLEAN);
        }

        if (raw == Byte.class || raw == byte.class) {
            return Optional.of(BYTE);
        }

        if (raw == Character.class || raw == char.class) {
            return Optional.of(CHAR);
        }

        if (raw == Short.class || raw == short.class) {
            return Optional.of(SHORT);
        }

        if (raw == Integer.class || raw == int.class) {
            return Optional.of(INTEGER);
        }

        if (raw == Long.class || raw == long.class) {
            return Optional.of(LONG);
        }

        if (raw == Float.class || raw == float.class) {
            return Optional.of(FLOAT);
        }

        if (raw == Double.class || raw == double.class) {
            return Optional.of(DOUBLE);
        }

        return Optional.empty();
    }

    public Object get(FieldDecoder a) throws IOException {
        return decoding.decode(a);
    }

    public void set(FieldEncoder encoder, Object value) throws IOException {
        this.encoding.encode(encoder, value);
    }

    public static interface DecodingFunction {
        public Object decode(FieldDecoder decoder) throws IOException;
    }

    public static interface EncodingFunction {
        public void encode(FieldEncoder decoder, Object value) throws IOException;
    }
}
