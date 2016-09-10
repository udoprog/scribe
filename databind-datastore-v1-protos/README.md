# Datastore V1 Protos Databind for Scribe

This library provides Datastore-based encoding for Scribe.

## Example Usage
You can setup the library like the following.

```java
final EntityResolver resolver = Scribe.defaultBuilder().build();
final DatastoreEntityV1ProtosMapper mapper = new DatastoreV1ProtosMapper(resolver);
```

Type initialization has to happen before encoding.

```java
@Data
public class Foo {
    private final String field;
}

final ConverterEncoding<Foo, Entity> foo = mapper.entityEncodingFor(Foo.class);
```

At this point, you can now efficiently encode or decode an instance of `Foo`.

```java
final Foo instance = new Foo("hello world");
final Entity encoded = foo.encode(instance);
final Foo result = foo.decode(encoded);

assert result.equals(instance);
```
