# TypeSafe Config Databind for TinyOGT

This library provides TypeSafe-based serialization for TinyOGT.

## Example Usage

You can setup the library like the following.

```java
final TypeSafeEntityMapper mapper = new TypeSafeEntityMapper(
    EntityMapper.defaultBuilder().register(new NativeAnnotationsModule()).build());
```

Type initialization has to happen before serialization.

```java
@Data
public class Foo {
    private final String field;
}

final TypeSafeEncoding<Foo> foo = mapper.encodingFor(Foo.class);
```

At this point, you can now efficiently serialize or deserialize an
instance of `Foo`.
 
```java
final Foo instance = new Foo("hello world");
final String encoded = foo.encodeAsString(instance);
final Foo result = foo.decodeFromString(encoded);

assert result.equals(instance) == true;
```

