package eu.toolchain.ogt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class Basic {
    @Param({"ogt", "jackson"})
    String type;

    @Param({"simple", "harder"})
    String instanceType;

    final Map<String, InstanceDeclaration> instances = new HashMap<>();

    {
        instances.put("simple", new InstanceDeclaration(Foo.class, new Foo()));
        instances.put("harder", new InstanceDeclaration(Bar.class,
            new Bar("Ted Johnsson", ImmutableList.of(1, 2, 3, 42, 96))));
    }

    Callable<String> benchmark;

    @Setup
    public void setup() throws Exception {
        InstanceDeclaration instance = instances.get(instanceType);

        if (instance == null) {
            throw new IllegalArgumentException("Illegal instance type: " + instanceType);
        }

        final Object i = instance.instance;

        switch (type) {
            case "ogt":
                final JsonFactory jsonFactory = new JsonFactory();

                final JacksonEntityMapper ogt = new JacksonEntityMapper(
                    EntityMapper.defaultBuilder().register(new JacksonAnnotationsModule()).build(),
                    jsonFactory);

                final JacksonTypeEncoding<Object> foo = ogt.encodingForType(instance.type);
                foo.encodeAsString(instance.instance);

                benchmark = () -> foo.encodeAsString(i);
                break;
            case "jackson":
                final ObjectMapper jackson = new ObjectMapper();
                jackson.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                jackson.writeValueAsString(i);

                benchmark = () -> jackson.writeValueAsString(i);
                break;
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    @Benchmark
    public void simple(Blackhole bh) throws Exception {
        bh.consume(benchmark.call());
    }

    @RequiredArgsConstructor
    public static class InstanceDeclaration {
        private final Class<?> type;
        private final Object instance;
    }

    public static class Foo {
        @JsonCreator
        public Foo() {
        }
    }

    @Data
    public static class Bar {
        private final String name;
        private final List<Integer> hobbies;

        @JsonCreator
        public Bar(
            @JsonProperty("name") final String name,
            @JsonProperty("hobbies") final List<Integer> hobbies
        ) {
            this.name = name;
            this.hobbies = hobbies;
        }
    }
}
