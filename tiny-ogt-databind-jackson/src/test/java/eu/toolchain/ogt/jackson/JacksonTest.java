package eu.toolchain.ogt.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.toolchain.ogt.EntityMapper;
import eu.toolchain.ogt.JacksonAnnotationsModule;
import eu.toolchain.ogt.JacksonEntityMapper;
import eu.toolchain.ogt.JacksonTypeEncoding;
import lombok.Data;
import lombok.experimental.Builder;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class JacksonTest {
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

    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    private JacksonTypeEncoding<Foo> foo;

    @Before
    public void setup() throws Exception {
        final JacksonEntityMapper mapper = new JacksonEntityMapper(
            EntityMapper.defaultBuilder().register(new JacksonAnnotationsModule()).build(),
            JSON_FACTORY);

        foo = mapper.encodingFor(Foo.class);
    }

    @Test
    public void testEncode() throws Exception {
        final String encoded = this.foo.encodeAsString(FOO);
        assertEquals(FOO, this.foo.decodeFromString(encoded));
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

        @JsonCreator
        public Bar(
            @JsonProperty("value") final double value,
            @JsonProperty("map") final Map<String, String> map
        ) {
            this.value = value;
            this.map = map;
        }
    }
}
