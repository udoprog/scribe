package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import eu.toolchain.ogt.type.TypeMapping;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JacksonTypeDecoder implements TypeDecoder<JsonNode> {
    private static final BaseEncoding BASE64 = BaseEncoding.base64();

    @Override
    public Object decodeBytesField(JavaType type, byte[] bytes) {
        throw new RuntimeException("not supported");
    }

    @Override
    public byte[] decodeBytes(JsonNode node) {
        return node.visit(new JsonNode.Visitor<byte[]>() {
            @Override
            public byte[] visitString(final JsonNode.StringJsonNode string) {
                return BASE64.decode(string.getValue());
            }
        });
    }

    @Override
    public short decodeShort(JsonNode node) {
        return node.visit(new JsonNode.Visitor<Short>() {
            @Override
            public Short visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return (short) numberNode.getValue();
            }

            @Override
            public Short visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return (short) floatNode.getValue();
            }
        });
    }

    @Override
    public int decodeInteger(JsonNode node) {
        return node.visit(new JsonNode.Visitor<Integer>() {
            @Override
            public Integer visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return (int) numberNode.getValue();
            }

            @Override
            public Integer visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return (int) floatNode.getValue();
            }
        });
    }

    @Override
    public long decodeLong(JsonNode node) {
        return node.visit(new JsonNode.Visitor<Long>() {
            @Override
            public Long visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return numberNode.getValue();
            }

            @Override
            public Long visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return (long) floatNode.getValue();
            }
        });
    }

    @Override
    public float decodeFloat(JsonNode node) {
        return node.visit(new JsonNode.Visitor<Float>() {
            @Override
            public Float visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return (float) numberNode.getValue();
            }

            @Override
            public Float visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return (float) floatNode.getValue();
            }
        });
    }

    @Override
    public double decodeDouble(JsonNode node) {
        return node.visit(new JsonNode.Visitor<Double>() {
            @Override
            public Double visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return (double) numberNode.getValue();
            }

            @Override
            public Double visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return floatNode.getValue();
            }
        });
    }

    @Override
    public boolean decodeBoolean(JsonNode node) {
        return node.visit(new JsonNode.Visitor<Boolean>() {
            @Override
            public Boolean visitBoolean(final JsonNode.BooleanJsonNode booleanNode) {
                return booleanNode.getValue();
            }
        });
    }

    @Override
    public byte decodeByte(JsonNode node) {
        return node.visit(new JsonNode.Visitor<Byte>() {
            @Override
            public Byte visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return (byte) numberNode.getValue();
            }

            @Override
            public Byte visitFloat(final JsonNode.FloatJsonNode floatNode) {
                return (byte) floatNode.getValue();
            }
        });
    }

    @Override
    public char decodeCharacter(JsonNode node) {
        return node.visit(new JsonNode.Visitor<Character>() {
            @Override
            public Character visitString(final JsonNode.StringJsonNode string) {
                return string.getValue().charAt(0);
            }
        });
    }

    @Override
    public Date decodeDate(JsonNode node) {
        return node.visit(new JsonNode.Visitor<Date>() {
            @Override
            public Date visitNumber(final JsonNode.NumberJsonNode numberNode) {
                return new Date(numberNode.getValue());
            }
        });
    }

    @Override
    public List<?> decodeList(TypeMapping value, Context path, JsonNode node) throws IOException {
        return node.visit(new JsonNode.Visitor<List<?>>() {
            @Override
            public List<?> visitList(final JsonNode.ListJsonNode node) {
                final ImmutableList.Builder<Object> list = ImmutableList.builder();

                int index = 0;

                for (final JsonNode v : node.getValues()) {
                    list.add(value.decode(JacksonTypeDecoder.this, path.push(index++), v));
                }

                return list.build();
            }
        });
    }

    @Override
    public Map<?, ?> decodeMap(TypeMapping key, TypeMapping value, Context path, JsonNode node)
        throws IOException {
        return node.visit(new JsonNode.Visitor<Map<?, ?>>() {
            @Override
            public Map<?, ?> visitObject(final JsonNode.ObjectJsonNode node) {
                final ImmutableMap.Builder<String, Object> map = ImmutableMap.builder();

                for (final Map.Entry<String, JsonNode> e : node.getFields().entrySet()) {
                    final String k = e.getKey();
                    final Object v =
                        value.decode(JacksonTypeDecoder.this, path.push(k), e.getValue());
                    map.put(k, v);
                }

                return map.build();
            }
        });
    }

    @Override
    public String decodeString(JsonNode node) {
        return node.visit(new JsonNode.Visitor<String>() {
            @Override
            public String visitString(final JsonNode.StringJsonNode string) {
                return string.getValue();
            }
        });
    }

    @Override
    public EntityDecoder<JsonNode> decodeEntity(JsonNode node) {
        final Map<String, JsonNode> fields =
            node.visit(new JsonNode.Visitor<Map<String, JsonNode>>() {
                @Override
                public Map<String, JsonNode> visitObject(final JsonNode.ObjectJsonNode node) {
                    return node.getFields();
                }
            });

        return new JacksonEntityDecoder(fields, this);
    }
}
