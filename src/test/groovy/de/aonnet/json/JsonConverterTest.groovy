/*
 * Copyright (c) 2011, Thomas Westphal
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

import org.junit.Test

class JsonConverterTest {

    private final static String JSON_STRING = '{"de.aonnet.json.Book":{"title":"Die Entwicklung des Berliner Flaschenbiergeschaefts","author":{"name":"Gustav Stresemann"}}}'
    private final static Map JSON_MAP = ["de.aonnet.json.Book": ["title": "Die Entwicklung des Berliner Flaschenbiergeschaefts", "author": ["name": "Gustav Stresemann"]]]
    private final static Map MAP_FOR_CREATE = [title: "Die Entwicklung des Berliner Flaschenbiergeschaefts", author: new Author(name: "Gustav Stresemann", nickName: "Gusti")]

    @Test
    public void testBeanToJson() {
        assert JSON_STRING == JsonConverter.toJsonString(new Book(MAP_FOR_CREATE))
    }

    @Test
    public void testBeanToMap() {
        assert JSON_MAP == JsonConverter.toJsonMap(new Book(MAP_FOR_CREATE))
    }

    @Test
    public void testJsonToBean() {
        assert JsonConverter.newInstanceFromJsonString(JSON_STRING) == new Book(MAP_FOR_CREATE)
    }

    @Test
    public void testMapToBean() {
        assert JsonConverter.newInstanceFromJsonMap(JSON_MAP) == new Book(MAP_FOR_CREATE)
    }

    @Test
    public void testBeanToJsonAndBackToBean() {
        Book bookBean = new Book(MAP_FOR_CREATE)
        String jsonBook = JsonConverter.toJsonString(bookBean)
        Book bookBean2 = JsonConverter.newInstanceFromJsonString(jsonBook)

        assert bookBean.properties == bookBean2.properties
    }

    @Test
    public void testBeanToMapAndBackToBean() {
        Book bookBean = new Book(MAP_FOR_CREATE)
        Map jsonBook = JsonConverter.toJsonMap(bookBean)
        Book bookBean2 = JsonConverter.newInstanceFromJsonMap(jsonBook)

        assert bookBean == bookBean2
    }
}
