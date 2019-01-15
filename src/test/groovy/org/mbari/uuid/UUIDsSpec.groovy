package org.mbari.uuid

import spock.lang.Specification

/**
 * @author Brian Schlining
 * @since 2019-01-15T13:15:00
 */
class UUIDsSpec extends Specification {

    def "converting a uuid1 to uuid6"() {

        given:
        def uuid1 = UUID.fromString("37f9f6ce-190a-11e9-9d6a-0242ac1c0002")

        when:
        def uuid6 = UUIDs.uuid6(uuid1);

        then:
        uuid6.toString() == "1e9190a3-7f9f-66ce-9d6a-0242ac1c0002"

    }
}
