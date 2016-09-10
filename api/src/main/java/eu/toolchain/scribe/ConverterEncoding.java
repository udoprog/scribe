package eu.toolchain.scribe;

import java.util.function.Function;

public interface ConverterEncoding<Source, Target> {
  Target encode(Source source);

  Source decode(Target target);

  default StringEncoding<Source> toStringEncoding(
      Function<Target, String> toString, Function<String, Target> fromString
  ) {
    final ConverterEncoding<Source, Target> parent = this;

    return new StringEncoding<Source>() {
      @Override
      public String encode(final Source instance) {
        return toString.apply(parent.encode(instance));
      }

      @Override
      public Source decode(final String source) {
        return parent.decode(fromString.apply(source));
      }
    };
  }
}
