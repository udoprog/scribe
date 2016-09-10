package eu.toolchain.scribe;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;

public interface Context {
  Context ROOT = new Root();

  Context parent();

  default ContextException error(Throwable e) {
    return new ContextException(this, e);
  }

  default ContextException error(String message) {
    return new ContextException(this, message);
  }

  default ContextException error(String message, Throwable e) {
    return new ContextException(this, message, e);
  }

  default String path() {
    if (this == ROOT) {
      return "<empty>";
    }

    final ArrayList<Context> parts = new ArrayList<>();

    Context current = this;

    while (current != ROOT) {
      parts.add(current);
      current = current.parent();
    }

    final StringBuilder builder = new StringBuilder();

    Collections.reverse(parts);

    for (final Context p : parts) {
      if (p instanceof Field) {
        final Field field = (Field) p;

        if (builder.length() > 0) {
          builder.append(".");
        }

        builder.append(field.getField());
        continue;
      }

      if (p instanceof Index) {
        builder.append("[" + ((Index) p).getIndex() + "]");
        continue;
      }
    }

    return builder.toString();
  }

  default Context push(int index) {
    return new Index(this, index, false);
  }

  default Context pushRoot(int index) {
    return new Index(this, index, true);
  }

  default Context push(String name) {
    return new Field(this, name, false);
  }

  default Context pushRoot(String name) {
    return new Field(this, name, true);
  }

  class Root implements Context {
    @Override
    public Context parent() {
      throw new IllegalStateException("no parent");
    }
  }

  @Data
  class Field implements Context {
    private final Context parent;
    private final String field;
    private final boolean root;

    @Override
    public Context parent() {
      return parent;
    }

    @Override
    public String toString() {
      return field;
    }
  }

  @Data
  class Index implements Context {
    private final Context parent;
    private final int index;
    private final boolean root;

    @Override
    public Context parent() {
      return parent;
    }

    @Override
    public String toString() {
      return Integer.toString(index);
    }
  }
}
