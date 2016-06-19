package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.experimental.Builder;
import org.junit.Before;
import org.junit.Test;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public abstract class AbstractDatabindTest<T> {
    private static final Map<String, String> MAP =
        ImmutableMap.of("hello", "world", "hello2", "world2");
    private static final Bar BAR1 = new Bar(0.1d, MAP);
    private static final Bar BAR2 = new Bar(0.2d, MAP);
    private static final Foo FOO = Foo
        .builder()
        .missing(Optional.empty())
        .existing(Optional.of("existing"))
        .field("hello world")
        .bars(ImmutableList.of(BAR1, BAR2))
        .build();

    private StringEncoding<Foo> foo;

    protected abstract <S> StringEncoding<S> encodingFor(TypeReference<S> type);

    protected abstract <S> StringEncoding<S> encodingFor(Class<S> type);

    @Before
    public void setup() throws Exception {
        foo = encodingFor(Foo.class);
    }

    @Test
    public void testEncode() throws Exception {
        roundTrip(this.foo, FOO);
    }

    @Data
    public static class TestGeneric<T> {
        private final List<T> list;
    }

    /**
     * Type variable T is unknown. Mapping should be impossible.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGenericFail() throws Exception {
        encodingFor(TestGeneric.class);
    }

    @Test
    public void testGenericTypeReference() throws Exception {
        final StringEncoding<TestGeneric<String>> encoding =
            encodingFor(new TypeReference<TestGeneric<String>>() {
            });

        roundTrip(encoding, new TestGeneric<>(ImmutableList.of("hello")));
    }

    @Data
    @Builder
    public static class Foo {
        private final Optional<String> missing;
        private final Optional<String> existing;
        private final String field;
        private final List<Bar> bars;
    }

    @Data
    public static class Bar {
        private final double value;
        private final Map<String, String> map;

        @ConstructorProperties({"value", "map"})
        public Bar(final double value, final Map<String, String> map) {
            this.value = value;
            this.map = map;
        }
    }

    private <S> void roundTrip(final StringEncoding<S> encoding, final S instance) {
        final String encoded = encoding.encodeAsString(instance);
        assertEquals(instance, encoding.decodeFromString(encoded));
    }
}
