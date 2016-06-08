package eu.toolchain.ogt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Data;

public interface Context {
    public static final Context ROOT = new Root();

    Context parent();

    default RuntimeException error(String message) {
        return new MappingException(this, message);
    }

    default MappingException error(String message, Throwable e) {
        return new MappingException(this, message, e);
    }

    default String path() {
        if (this == ROOT) {
            return "<empty>";
        }

        final ImmutableList.Builder<Context> parts = ImmutableList.builder();

        Context current = this;

        while (current != ROOT) {
            parts.add(current);
            current = current.parent();
        }

        final StringBuilder builder = new StringBuilder();

        for (final Context p : Lists.reverse(parts.build())) {
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
        return new Index(this, index);
    }

    default Context push(String name) {
        return new Field(this, name);
    }

    static class Root implements Context {
        @Override
        public Context parent() {
            throw new IllegalStateException("no parent");
        }
    }

    @Data
    static class Field implements Context {
        private final Context parent;
        private final String field;

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
    static class Index implements Context {
        private final Context parent;
        private final int index;

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
