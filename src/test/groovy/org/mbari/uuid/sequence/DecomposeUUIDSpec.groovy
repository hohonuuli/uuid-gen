package org.mbari.uuid.sequence

import org.mbari.uuid.UUIDs
import spock.lang.Specification

import java.time.Instant

/**
 * @author Brian Schlining
 * @since 2019-01-15T15:37:00
 */
class DecomposeUUIDSpec extends Specification {

    def "decomposing a counter UUID"() {

        given:
        def uuid = UUIDs.uuidCounter()

        when:
        def d = new DecomposedUUID(uuid)

        then:
        d.getTimestamp().epochSecond - Instant.now().epochSecond < 1000L
        d.getVersion() == Shared.VERSION
        d.getProcessId() == Shared.PID
        def mac = d.getMacFragment()
        mac[3] == Shared.MAC[3]
        mac[4] == Shared.MAC[4]
        mac[5] == Shared.MAC[5]

    }

    def "decomposing a sequence UUID"() {

        given:
        def uuid = UUIDs.uuidSequence()

        when:
        def d = new DecomposedUUID(uuid)

        then:
        d.getTimestamp().epochSecond - Instant.now().epochSecond < 1000L
        d.getVersion() == Shared.VERSION
        d.getProcessId() == Shared.PID
        def mac = d.getMacFragment()
        mac[3] == Shared.MAC[3]
        mac[4] == Shared.MAC[4]
        mac[5] == Shared.MAC[5]

    }
}
