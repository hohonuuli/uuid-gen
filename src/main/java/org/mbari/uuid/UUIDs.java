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
package org.mbari.uuid;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import java.util.UUID;
import org.mbari.uuid.sequence.CounterSequenceGenerator;
import org.mbari.uuid.sequence.TimeSequenceGenerator;

/**
 * UUIDs
 */
public class UUIDs {

    private static final TimeBasedGenerator timeBasedGenerator = getTimeBasedGenerator();

    private static TimeBasedGenerator getTimeBasedGenerator() {
        TimeBasedGenerator tbg;
        try {
            tbg = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
        }
        catch (Exception e) {

            // No network card is available
            tbg = Generators.timeBasedGenerator();
        }

        return tbg;
    }

    /**
     * @return
     */
    public static UUID uuid1() {
        return timeBasedGenerator.generate();
    }

    /**
     * @return
     */
    public static UUID uuid4() {
        return UUID.randomUUID();
    }

    /**
     * https://bradleypeabody.github.io/uuidv6/
     * @return
     */
    public static UUID uuid6() {
        return uuid6(uuid1());
    }

    /**
     * https://bradleypeabody.github.io/uuidv6/
     *
     * @param uuid1
     * @return
     */
    public static UUID uuid6(UUID uuid1) {
        long ut = uuid1.getMostSignificantBits();
        long ut2 = ((ut >> 32) & 0x0FFF) |              // 12 least significant bits
                (0x6000) |                              // version number
                ((ut >> 28) & 0x0000000FFFFF0000L) |    // next 20 bits
                ((ut << 20) & 0x000FFFF000000000L) |    // next 16 bits
                (ut << 52);                             // 12 most significant bits

        return new UUID(ut2, uuid1.getLeastSignificantBits());
    }

    /**
     * @return
     */
    public static UUID uuidCounter() {
        return CounterSequenceGenerator.nextUuid();
    }

    /**
     * @return
     */
    public static UUID uuidSequence() {
        return TimeSequenceGenerator.nextUuid();
    }


    /**
     * <a href="http://www.informit.com/articles/article.aspx?p=25862&seqNum=7">COMBined UUID</a>
     *
     * This is a modified version of the COMB used n the article as we are using a higher
     * resolution timestamp. All of the least significant bits represent timestamp.
     * The timestamp bytes are ordered as:
     *
     * <pre>
     * 0 1 2 3 4 5 6 7 8
     * |               ` slowest changing
     * ` fastest changing
     * </pre>
     *
     * This should satisfy SQL servers insert order if you use a UUID as primary,
     * clustered key.
     * @return
     */
    public static UUID comb() {

        // Generate time bits. Reverse byte order so that time is sorted
        // using SQL servers weird sort order.
        UUID uuid6 = uuid6();
        long time = uuid6.getMostSignificantBits();
        long emit = Long.reverseBytes(time);

        // Generate random bits
        UUID uuid4 = uuid4();
        return new UUID(uuid4.getMostSignificantBits(), emit);

    }

}
