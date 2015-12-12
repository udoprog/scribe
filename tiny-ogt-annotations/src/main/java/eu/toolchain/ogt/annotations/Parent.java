package eu.toolchain.ogt.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark which parent a type has.
 *
 * @see EntityKey#getParent()
 * @author udoprog
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Parent {
    Class<?> value();
}
