package de.aonnet.json

import org.junit.Test

class JsonSupportTransformationTest {

    private final static String JSON_STRING = '{"de.aonnet.json.Book":{"title":"Die Entwicklung des Berliner Flaschenbiergeschaefts","author":{"name":"Gustav Stresemann"}}}'
    private final static Map JSON_MAP = ["de.aonnet.json.Book": ["title": "Die Entwicklung des Berliner Flaschenbiergeschaefts", "author": ["name": "Gustav Stresemann"]]]
    private final static Map MAP_FOR_CREATE = [title: "Die Entwicklung des Berliner Flaschenbiergeschaefts", author: new Author(name: "Gustav Stresemann", nickName: "Gusti")]

    @Test
    public void testBookMethods() {
        Book bean = new Book(MAP_FOR_CREATE)

        assert bean.metaClass.methods.find { it.name == 'toJsonString'} != null
        assert bean.metaClass.methods.find { it.name == 'toJsonMap'} != null
        assert bean.metaClass.methods.find { it.name == 'newInstanceFromJsonString'} != null
        assert bean.metaClass.methods.find { it.name == 'newInstanceFromJsonMap'} != null
    }

    @Test
    public void testBeanToJson() {
        assert JSON_STRING == new Book(MAP_FOR_CREATE).toJsonString()
    }

    @Test
    public void testBeanToMap() {
        assert JSON_MAP == new Book(MAP_FOR_CREATE).toJsonMap()
    }

    @Test
    public void testJsonToBean() {
        assert new Book(MAP_FOR_CREATE) == Book.newInstanceFromJsonString(JSON_STRING)
    }

    @Test
    public void testMapToBean() {
        assert  new Book(MAP_FOR_CREATE) == Book.newInstanceFromJsonMap(JSON_MAP)
    }

    @Test
    public void testBeanToJsonAndBackToBean() {

        Book bookBean = new Book(MAP_FOR_CREATE)
        String jsonBook = bookBean.toJsonString()
        Book bookBean2 = Book.newInstanceFromJsonString(jsonBook)

        assert bookBean.properties == bookBean2.properties
    }

    @Test
    public void testBeanToMapAndBackToBean() {

        Book bookBean = new Book(MAP_FOR_CREATE)
        Map jsonBook = bookBean.toJsonMap()
        Book bookBean2 = Book.newInstanceFromJsonMap(jsonBook)

        assert bookBean == bookBean2
    }
}
