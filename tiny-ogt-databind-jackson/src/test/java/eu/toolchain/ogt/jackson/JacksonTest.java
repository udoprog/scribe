package eu.toolchain.ogt.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import eu.toolchain.ogt.EntityMapper;
import eu.toolchain.ogt.JacksonAnnotationsModule;
import eu.toolchain.ogt.JacksonEncodingFactory;
import eu.toolchain.ogt.JsonNode;
import eu.toolchain.ogt.TypeEncoding;
import eu.toolchain.ogt.TypeEncodingProvider;
import lombok.Data;
import lombok.experimental.Builder;

public class JacksonTest {
    private static final Map<String, String> MAP =
            ImmutableMap.of("hello", "world", "hello2", "world2");
    private static final Bar BAR1 = new Bar(0.1d, MAP);
    private static final Bar BAR2 = new Bar(0.2d, MAP);
    private static final Foo FOO =
            Foo.builder().missing(Optional.empty()).existing(Optional.of("existing"))
                    .field("hello world").bars(ImmutableList.of(BAR1, BAR2)).build();

    private static final JsonFactory JSON_FACTORY = new JsonFactory();

    private TypeEncoding<Foo, String> foo;
    private TypeEncodingProvider<String> provider;

    @Before
    public void setup() {
        final EntityMapper mapper =
                EntityMapper.defaultBuilder().register(new JacksonAnnotationsModule()).build();

        provider = mapper.providerFor(new JacksonEncodingFactory(JSON_FACTORY));

        foo = provider.encodingFor(Foo.class);
    }

    @Test
    public void testEncode() throws Exception {
        final String encoded = this.foo.encode(FOO);
        assertTrue(encoded instanceof String);
        assertEquals(FOO, this.foo.decode(encoded));
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
        public Bar(@JsonProperty("value") final double value,
                @JsonProperty("map") final Map<String, String> map) {
            this.value = value;
            this.map = map;
        }
    }
}
