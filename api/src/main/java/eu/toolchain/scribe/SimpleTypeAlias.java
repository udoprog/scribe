package eu.toolchain.scribe;

import eu.toolchain.scribe.reflection.JavaType;
import lombok.Data;
import lombok.ToString;

import java.util.function.Function;

@Data
@ToString(of = {"to", "from"})
public class SimpleTypeAlias<From, To> implements TypeAlias<From, To> {
  private final JavaType to;
  private final JavaType from;

  private final Function<To, From> convertTo;
  private final Function<From, To> convertFrom;

  @Override
  public JavaType getFromType() {
    return from;
  }

  @Override
  public JavaType getToType() {
    return to;
  }

  @Override
  public Mapping<To> apply(final Mapping<From> mapping) {
    return new TypeAliasMapping<>(to, mapping, convertTo, convertFrom);
  }

  public static <From, To> TypeAlias<From, To> simpleAlias(
      Class<To> to, Class<From> from, Function<To, From> toConverter,
      Function<From, To> fromConverter
  ) {
    return new SimpleTypeAlias<>(JavaType.of(to), JavaType.of(from), toConverter, fromConverter);
  }
}
