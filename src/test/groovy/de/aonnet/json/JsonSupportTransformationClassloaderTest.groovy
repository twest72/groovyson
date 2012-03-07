package de.aonnet.json

import org.junit.Test

class JsonSupportTransformationClassloaderTest {

    private final static String JSON_STRING = '{"de.aonnet.json.Book":{"title":"Die Entwicklung des Berliner Flaschenbiergeschaefts","author":{"name":"Gustav Stresemann"}}}'
    private final static Map JSON_MAP = ["de.aonnet.json.Book": ["title": "Die Entwicklung des Berliner Flaschenbiergeschaefts", "author": ["name": "Gustav Stresemann"]]]
    private final static Map MAP_FOR_CREATE = [title: "Die Entwicklung des Berliner Flaschenbiergeschaefts", author: new Author(name: "Gustav Stresemann", nickName: "Gusti")]
    private final static Map MAP_FOR_CREATE_WITHOUT_EXCLUDES = [title: "Die Entwicklung des Berliner Flaschenbiergeschaefts", author: new Author(name: "Gustav Stresemann")]

    @Test
    public void testBeanToJson() {
        Class bookClass = createBookClass();
        assert JSON_STRING == createBookBean(bookClass, MAP_FOR_CREATE).toJsonString()
    }

    @Test
    public void testBeanToMap() {
        Class bookClass = createBookClass();
        assert JSON_MAP == createBookBean(bookClass, MAP_FOR_CREATE).toJsonMap()
    }

    @Test
    public void testJsonToBean() {
        Class bookClass = createBookClass();
        def beanFromJson = bookClass.newInstanceFromJsonString(JSON_STRING)
        def bean = createBookBean(bookClass, MAP_FOR_CREATE_WITHOUT_EXCLUDES)

        // Leider nicht gleich, da beim Instanziieren verschiedene Classloader benutzt werden...
        assert beanFromJson != bean

        assert beanFromJson.toString() == bean.toString()
    }

    @Test
    public void testMapToBean() {
        Class bookClass = createBookClass();
        def beanFromJson = bookClass.newInstanceFromJsonMap(JSON_MAP)
        def bean = createBookBean(bookClass, MAP_FOR_CREATE_WITHOUT_EXCLUDES)

        // Leider nicht gleich, da beim Instanziieren verschiedene Classloader benutzt werden...
        assert beanFromJson != bean

        assert beanFromJson.toString() == bean.toString()
    }

    private def createBookBean(Class bookClass, Map args) {
        def bean = bookClass.newInstance(args)

        assert bean.metaClass.methods.find { it.name == 'toJsonString'} != null
        assert bean.metaClass.methods.find { it.name == 'toJsonMap'} != null
        assert bean.metaClass.methods.find { it.name == 'newInstanceFromJsonString'} != null
        assert bean.metaClass.methods.find { it.name == 'newInstanceFromJsonMap'} != null

        return bean
    }

    private Class createBookClass() {
        def file = new File('./src/test/groovy/de/aonnet/json/Book.groovy')
        assert file.exists()

        GroovyClassLoader invoker = new GroovyClassLoader()
        Class clazz = invoker.parseClass(file)

        assert clazz.metaClass.methods.find { it.name == 'toJsonString'} != null
        assert clazz.metaClass.methods.find { it.name == 'toJsonMap'} != null
        assert clazz.metaClass.methods.find { it.name == 'newInstanceFromJsonString'} != null
        assert clazz.metaClass.methods.find { it.name == 'newInstanceFromJsonMap'} != null

        return clazz
    }
}
