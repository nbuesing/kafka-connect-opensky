package com.github.nbuesing.kafka.connect.opensky.util

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ListUtilsSpec extends Specification {

    def 'partition(#list, #parts)'() {

        when:

        List<List<String>> result = ListUtils.partition(list, parts)


        then:
        assert result == expected

        where:
        list                      | parts | expected
        ['a']                     | 1     | [['a']]
        ['a', 'b']                | 1     | [['a', 'b']]
        ['a', 'b', 'c']           | 1     | [['a', 'b', 'c']]
        ['a', 'b']                | 1     | [['a', 'b']]
        ['a', 'b']                | 2     | [['a'], ['b']]
        ['a', 'b']                | 3     | [['a'], ['b']]
        ['a', 'b', 'c', 'd']      | 1     | [['a', 'b', 'c', 'd']]
        ['a', 'b', 'c', 'd']      | 2     | [['a', 'b'], ['c', 'd']]
        ['a', 'b', 'c', 'd']      | 3     | [['a'], ['b'], ['c', 'd']]
        ['a', 'b', 'c', 'd']      | 4     | [['a'], ['b'], ['c'], ['d']]
        ['a', 'b', 'c', 'd', 'e'] | 1     | [['a', 'b', 'c', 'd', 'e']]
        ['a', 'b', 'c', 'd', 'e'] | 2     | [['a', 'b'], ['c', 'd', 'e']]
        ['a', 'b', 'c', 'd', 'e'] | 3     | [['a'], ['b', 'c'], ['d', 'e']]
        ['a', 'b', 'c', 'd', 'e'] | 4     | [['a'], ['b'], ['c'], ['d', 'e']]
        ['a', 'b', 'c', 'd', 'e'] | 5     | [['a'], ['b'], ['c'], ['d'], ['e']]
        ['a', 'b', 'c', 'd', 'e'] | 6     | [['a'], ['b'], ['c'], ['d'], ['e']]
        ['a', 'b', 'c', 'd', 'e'] | 7     | [['a'], ['b'], ['c'], ['d'], ['e']]
    }
}
