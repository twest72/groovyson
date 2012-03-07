package de.aonnet.json

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode(excludes = 'nickName')
class Author {

    String name

    @JsonExclude
    String nickName
}
