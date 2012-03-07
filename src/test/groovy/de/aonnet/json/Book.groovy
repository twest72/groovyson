package de.aonnet.json

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@JsonSupport
@ToString
@EqualsAndHashCode
class Book {

    Author author
    String title
}
