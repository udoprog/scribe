# Jackson Annotations for Scribe

Contains the [`JacksonAnnotationsModule`][module-class] that provides
support for Jackson annotations.

The annotations module is installed like the following.

```java
final EntityMapper m =  EntityMapper
    .defaultBuilder()
    .register(new JacksonAnnotationsModule())
    .build();
```

[module-class]: src/main/java/eu/toolchain/scribe/JacksonAnnotationsModule.java