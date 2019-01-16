/*
 * Copyright 2019 Monterey Bay Aquarium Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
