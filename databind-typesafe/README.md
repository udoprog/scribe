# TypeSafe Config Databind for Scribe

This library provides [TypeSafe][typesafe]-based serialization for
Scribe.

[typesafe]: https://github.com/typesafehub/config

## Example Usage

You can setup the library like the following.

```java
final EntityResolver resolver = Scribe
    .defaultBuilder()
    .install(new NativeAnnotationsModule())
    .build();

final TypeSafeMapper mapper = new TypeSafeMapper(resolver);
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
final String encoded = foo.encodeAsString(instance);
final Foo result = foo.decodeFromString(encoded);

assert result.equals(instance) == true;
```

