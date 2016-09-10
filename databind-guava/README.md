# Guava Databind for Scribe

This library provides Guava support for Scribe.

#### com.google.base.Optional

Allows Guava's Optional to behave the same as `java.util.Optional` to
act as a carrier for optional fields and values.

## Example Usage

You can setup the library like the following.

```java
final EntityResolver resolver = Scribe
    .defaultBuilder()
    .install(new GuavaModule())
    .build();
```
