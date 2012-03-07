/*
 * Copyright (c) 2012, Thomas Westphal
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

        if (beanData.size() <= 0) {
            return null
        }

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
