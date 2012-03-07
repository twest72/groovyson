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
