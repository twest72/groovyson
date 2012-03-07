package de.aonnet.json

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import org.codehaus.groovy.transform.GroovyASTTransformationClass

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass("de.aonnet.json.JsonSupportTransformation")
public @interface JsonSupport {
}
