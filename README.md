# Scribe

Scribe is a databinding library.

Many encoding libraries implement the idea of databinding, but they are
typically tied to that single library. Scribe is a more general
implementation which does not target a single encoding.
 
Classes are reflexively inspected to get information of which fields
they have, how they can be constructed, and their data accessed. This is
then used to integrated with existing third party solutions like
[Jackson][jackson] and [TypeSafe Config][typesafe] to provide the actual
encoding.

Many libraries implement the idea of data-binding, but tend to do it in
different ways. They provide their own annotations, procedures, have
have various shortcomings or flaws.
This makes interoperability between them a nightmare. It's not uncommon
to have distinct set of classes just to interact with different data
formats.

[jackson]: https://github.com/FasterXML/jackson
[typesafe]: https://github.com/typesafehub/config

# Principles

Only support immutable types.

Never decode a field as `null` or
[default primitive values][default-values]. Look at
[Optional Support](#optional-support) if you want optional values and
fields. Various representations of `null` are treated equally to the
value being absent as much as possible.

Make use of Java 8 features to make things more concise. Scribe requires
that you run an up-to-date Java version.

As little inspection at runtime as possible. With reflection it is hard
to avoid, but the API encourages much early initialization to fail as
early as possible.

Shallow stack traces. The instances you use in production have as little
indirection as possible to improve performance and troubleshooting.

[default-values]: http://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html

# Usage

Types are general converted into a [`TypeMapping`][typemapping] using the
[`EntityResolver`][entityresolver], like the following example.

```java
public class Person {
  private final String name;
  private final Optional<String> title;

  @ConstructorProperties({"name", "title"})
  public Person(final String name, final Optional<String> title) {
    this.name = name;
    this.title = title;
  }

  public String getName() {
    return name;
  }

  public Optional<String> getTitle() {
    return title;
  }
}
```

```java
EntityResolver resolver = EntityMapper.defaultBuilder().build();
TypeMapping person = resolver.mapping(JavaType.of(Person.class));
```

The following is an example using the provided Jackson support to encode and
decode JSON.

```java
final JacksonEntityMapper mapper =
    new JacksonEntityMapper(
      EntityMapper
        .defaultBuilder()
        .register(new JacksonAnnotationsModule())
        .build(),
      new JsonFactory());

final JacksonEncoding<Person> encoding = mapper.encodingFor(Person.class);

final String encoded = encoding.encodeAsString(new Person("Jane Doe", Optional.of("Doctor")));
final Person person = encoding.decodeFromString(encoded);
```

[typemapping]: /api/src/main/java/eu/toolchain/scribe/typemapping/TypeMapping.java
[entityresolver]: /api/src/main/java/eu/toolchain/scribe/EntityResolver.java

## Optional Support

Optional has first-class support in Scribe.

Any field wrapped in Optional may be absent when decoding an entity. Any
value wrapped in Optional may also be absent. If the value is absent it
is represented as the Optional's absent instance.

The following is an example of a class using an Optional field.

```java
public class Person {
  private final String name;
  private final Optional<String> title;

  @ConstructorProperties({"name", "title"})
  public Person(final String name, final Optional<String> title) {
    this.name = name;
    this.title = title;
  }

  public String getName() {
    return name;
  }

  public Optional<String> getTitle() {
    return title;
  }
}
```

`title` will _not_ be a required field if this entity was used. But we
also take great care in making sure you don't have to handle `null`.

## Custom Types

You want to support a custom type? Actually, you probably want to
translate this to some already well known type. For this case we support
aliasing.

Aliasing is when a type is translated to another type during
encoding/decoding. The following is an example of a simple alias for
`Character` to `String`.

```java
public class DefaultModule implements Module {
  @Override
  public <T> EntityMapperBuilder<T> register(EntityMapperBuilder<T> b) {
    final Function<Character, String> charToString =
        c -> new String(new char[]{c});

    final Function<String, Character> stringToChar = s -> s.charAt(0);

    /* isPrimitive matches both char and Character */
    b.typeAliasDetector(
      matchAlias(
        isPrimitive(char.class),
        type -> simpleAlias(Character.class, String.class, charToString, stringToChar)
      )
    );

    return b;
  }
}
```

All we need to do is describe how a `Character` is converted into a
`String` and vice-versa. The framework will do the rest.

## Libraries

* [Native Annotations Support](/annotations)
* [Jackson Annotations Support](/annotations-jackson)
* [Jackson Databind Support](/databind-jackson)
* [Guava Databind Support](/databind-guava)
* [TypeSafe Config Databind Support](/databind-typesafe)
