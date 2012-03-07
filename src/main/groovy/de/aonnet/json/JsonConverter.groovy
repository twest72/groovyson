package de.aonnet.json

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import java.lang.reflect.Field

class JsonConverter {

    private final static List<Class> SIMPLE_TYPES = [String.class, Number.class, Boolean.class, Character.class]

    private final static List<String> EXCLUDE_FIELD_NAMES = ['class', 'metaClass']

    static String toJsonString(def bean) {

        return createJsonBuilder(bean).toString()
    }

    static Map toJsonMap(def bean) {

        return createJsonBuilder(bean).content
    }

    private static JsonBuilder createJsonBuilder(def bean) {

        JsonBuilder builder = new JsonBuilder()
        builder {
            // the first pair is the bean (key -> classname, values -> bean properties)
            "${ bean.class.getName() }" buildProperties(bean)
        }

        return builder
    }

    private static Map buildProperties(def bean) {

        Map properties = [:]
        List excludeFieldNames = getExcludeFieldNames(bean)

        bean.properties.each {propName, propValue ->

            if (!excludeFieldNames.contains(propName)) {

                if (propValue == null || SIMPLE_TYPES.contains(propValue.getClass())) {
                    properties.put propName, propValue
                } else {
                    properties.put propName, buildProperties(propValue)
                }
            }
        }

        return properties
    }

    private static List getExcludeFieldNames(def bean) {

        List excludeFieldNames = []
        excludeFieldNames.addAll EXCLUDE_FIELD_NAMES

        bean.getClass().declaredFields.each { Field field ->

            if (!excludeFieldNames.contains(field.name) && field.getAnnotation(JsonExclude) != null) {
                excludeFieldNames << field.name
            }
        }

        return excludeFieldNames
    }

    static def newInstanceFromJsonString(String json) {

        Map beanData = new JsonSlurper().parseText(json)
        return newInstanceFromJsonMap(beanData)
    }

    static def newInstanceFromJsonMap(Map beanData) {

        String beanClassName
        Map beanProperties

        beanData.eachWithIndex { key, value, index ->

            // the first pair is the bean (key -> classname, values -> bean properties)
            if (index == 0) {
                beanClassName = key
                beanProperties = value
            }
        }

        return Class.forName(beanClassName).newInstance(beanProperties)
    }
}
