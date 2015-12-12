package eu.toolchain.ogt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark which kind the annotated type has.
 *
 * The default kind will otherwise by the canonical name of the type.
 *
 * @see EntityKey#getKind()
 * @author udoprog
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Kind {
    String value();
}
