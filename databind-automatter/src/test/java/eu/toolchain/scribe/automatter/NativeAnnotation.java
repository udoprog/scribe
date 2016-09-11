package eu.toolchain.scribe.automatter;

import eu.toolchain.scribe.annotations.Property;
import io.norberg.automatter.AutoMatter;

@AutoMatter
public interface NativeAnnotation {
  @Property("alias")
  String field();
}
