# Jackson Databind for TinyOGT

This library provides Jackson-based serialization for TinyOGT.

A companion module is provided that provides annotation support for
Jackson, see [tiny-ogt-annotations-jackson](/tiny-ogt-annotations-jackson).

## Example Usage

You can setup the library like the following.

```java
final JsonFactory json = new JsonFactory();

final JacksonEntityMapper mapper = new JacksonEntityMapper(
    EntityMapper.defaultBuilder().register(new JacksonAnnotationsModule()).build(),
    json);
```

Type initialization has to happen before serialization.

```java
@Data
public class Foo {
    private final String field;
}

final JacksonEncoding<Foo> foo = mapper.encodingFor(Foo.class);
```

At this point, you can now efficiently serialize or deserialize an
instance of `Foo`.
 
```java
final Foo instance = new Foo("hello world");
final String encoded = foo.encodeAsString(instance);
final Foo result = foo.decodeFromString(encoded);

assert result.equals(instance) == true;
```


