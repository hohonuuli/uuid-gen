/*
Copyright (c) 2013, Groupon, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
Neither the name of GROUPON nor the names of its contributors may be
used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.mbari.uuid.sequence;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

/**
 * This generates UUIDs in the following format:
 * <pre>
 *    wwwwwwww-xxxx-byyy-yyyy-zzzzzzzzzzzz
 *
 * w: counter value
 * x: process id
 * b: literal hex 'b' representing the UUID version
 * y: fragment of machine MAC address
 * z: UTC timestamp (milliseconds since epoch)
 * </pre>
 *
 * Examples of time sequentially generatied UUID:
 *
 * <pre>
 * c8c9cef9-7a7f-bd53-7a50-013e4e2afbde
 * 14951cfa-7a7f-bd53-7a50-013e4e2afbde
 * 6f5169fb-7a7f-bd53-7a50-013e4e2afbde
 * ba2da6fc-7a7f-bd53-7a50-013e4e2afbde
 * 06f8f3fc-7a7f-bd53-7a50-013e4e2afbde
 * 51c441fd-7a7f-bd53-7a50-013e4e2afbde
 * ac809efe-7a7f-bd53-7a50-013e4e2afbde
 * f75cdbff-7a7f-bd53-7a50-013e4e2afbde
 * </pre>
 *
 * Should be appropriate for storing in SQL Servers uniqueidentifier columns
 */
public class TimeSequenceGenerator {

    private static final AtomicInteger COUNTER = new AtomicInteger(
        new Random(System.nanoTime()).nextInt());
    private static final int INCREMENT = 198491317;

    /**
     * @return
     */
    public static java.util.UUID nextUuid() {
        byte[] content = new byte[16];
        long time = Instant.now()
                .toEpochMilli();

        // atomically add a large prime number to the count and get the previous value
        int count = COUNTER.addAndGet(INCREMENT);

        // switch the order of the count in 4 bit segments and place into content
        content[0] = (byte) (((count & 0xF) << 4) | ((count & 0xF0) >> 4));
        content[1] = (byte) (((count & 0xF00) >> 4) | ((count & 0xF000) >> 12));
        content[2] = (byte) (((count & 0xF0000) >> 12) | ((count & 0xF00000) >> 20));
        content[3] = (byte) (((count & 0xF000000) >> 20) | ((count & 0xF0000000) >> 28));

        // copy pid to content
        content[4] = (byte) (Shared.PID >> 8);
        content[5] = (byte) (Shared.PID);

        // place UUID version (hex 'b') in first four bits and piece of mac address in
        // the second four bits
        content[6] = (byte) (Shared.VERSION_DEC | (0xF & Shared.MAC[2]));

        // copy rest of mac address into content
        content[7] = Shared.MAC[3];
        content[8] = Shared.MAC[4];
        content[9] = Shared.MAC[5];

        // copy timestamp into content
        content[10] = (byte) (time >> 40);
        content[11] = (byte) (time >> 32);
        content[12] = (byte) (time >> 24);
        content[13] = (byte) (time >> 16);
        content[14] = (byte) (time >> 8);
        content[15] = (byte) (time);

        return new java.util.UUID(Shared.getMostSignificantBits(content),
                Shared.getLeastSignificantBits(content));

    }
}
