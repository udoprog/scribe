package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.asyncdatastoreclient.Entity;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import eu.toolchain.ogt.annotations.EntityCreator;
import eu.toolchain.ogt.annotations.Property;
import lombok.Data;
import lombok.experimental.Builder;

public class DatastoreTest {
    private static final Map<String, String> MAP =
            ImmutableMap.of("hello", "world", "hello2", "world2");
    private static final Bar BAR1 = new Bar(0.1d, MAP);
    private static final Bar BAR2 = new Bar(0.2d, MAP);
    private static final Foo FOO =
            Foo.builder().missing(Optional.empty()).existing(Optional.of("existing"))
                    .field("hello world").bars(ImmutableList.of(BAR1, BAR2)).build();

    private TypeEncoding<Foo, Entity> foo;
    private TypeEncodingProvider<Entity> provider;

    @Before
    public void setup() {
        final EntityMapper mapper = EntityMapper.nativeBuilder().build();

        provider = mapper.providerFor(new DatastoreEncodingFactory());

        foo = provider.encodingFor(Foo.class);
    }

    @Test
    public void testEncode() throws Exception {
        final Entity encoded = this.foo.encode(FOO);
        assertTrue(encoded instanceof Entity);
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

        @EntityCreator
        public Bar(@Property("value") final double value,
                @Property("map") final Map<String, String> map) {
            this.value = value;
            this.map = map;
        }
    }
}
