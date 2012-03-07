package de.aonnet.json

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.Target
import static java.lang.annotation.ElementType.FIELD
import static java.lang.annotation.RetentionPolicy.RUNTIME

@Target(value = FIELD)
@Retention(value = RUNTIME)
@Documented
@interface JsonExclude {
}
