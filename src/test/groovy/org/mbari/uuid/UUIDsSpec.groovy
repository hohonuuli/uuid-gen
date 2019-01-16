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
