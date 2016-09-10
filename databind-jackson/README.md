# Jackson Databind for Scribe

This library provides Jackson-based serialization for Scribe.

A companion module is provided that provides annotation support for
Jackson, see [scribe-annotations-jackson](/annotations-jackson).

## Example Usage

You can setup the library like the following.

```java
final EntityResolver resolver = Scribe
    .defaultBuilder()
    .install(new JacksonAnnotationsModule())
    .build();

final JacksonMapper mapper = new JacksonMapper(resolver);
```

Type initialization has to happen before serialization.

```java
@Data
public class Foo {
    private final String field;
}

final StringEncoding<Foo> foo = mapper.stringEncodingFor(Foo.class);
```

At this point, you can now efficiently serialize or deserialize an
instance of `Foo`.
 
```java
final Foo instance = new Foo("hello world");
final String encoded = foo.encode(instance);
final Foo result = foo.decode(encoded);

assert result.equals(instance);
```


