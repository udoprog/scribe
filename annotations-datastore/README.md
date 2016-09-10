# Datastore Annotations for Scribe

Contains the [`DatastoreAnnotationsModule`][module-class] that provides
support for Jackson annotations.

The annotations module is installed like the following.

```java
final Scribe m = Scribe
    .defaultBuilder()
    .install(new DatastoreAnnotationsModule())
    .build();
```

[module-class]: src/main/java/eu/toolchain/scribe/DatastoreAnnotationsModule.java
