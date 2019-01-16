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
import java.util.UUID;

public class DecomposedUUID {

    private final UUID uuid;
    private final byte[] content = new byte[16];
    private static final char[] HEX =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    public DecomposedUUID(UUID uuid) {
        this.uuid = uuid;
        constructFromLongs(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    private void constructFromLongs(long hi, long lo) {
        content[ 0] = (byte) (hi >> 56);
        content[ 1] = (byte) (hi >> 48);
        content[ 2] = (byte) (hi >> 40);
        content[ 3] = (byte) (hi >> 32);
        content[ 4] = (byte) (hi >> 24);
        content[ 5] = (byte) (hi >> 16);
        content[ 6] = (byte) (hi >>  8);
        content[ 7] = (byte) (hi      );
        content[ 8] = (byte) (lo >> 56);
        content[ 9] = (byte) (lo >> 48);
        content[10] = (byte) (lo >> 40);
        content[11] = (byte) (lo >> 32);
        content[12] = (byte) (lo >> 24);
        content[13] = (byte) (lo >> 16);
        content[14] = (byte) (lo >>  8);
        content[15] = (byte) (lo      );
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Extract MAC address fragment from raw UUID bytes, setting missing values to 0, thus the first 2 and a half bytes
     * will be 0, followed by 3 and a half bytes of the active MAC address when the UUID was generated.
     * @return Byte array of UUID fragment, or null for unrecognized format.
     */
    public byte[] getMacFragment() {
        if (getVersion() != 'b')
            return null;

        byte[] x = new byte[6];

        x[0] = 0;
        x[1] = 0;
        x[2] = (byte) (content[6] & 0xF);
        x[3] = content[7];
        x[4] = content[8];
        x[5] = content[9];

        return x;
    }

    /**
     * Extract process id from raw UUID bytes and return as int. This only applies for this type of UUID, for other
     * UUID types, such as the randomly generated v4, its not possible to discover process id, so -1 is returned.
     * @return Id of process that generated the UUID, or -1 for unrecognized format.
     */
    public int getProcessId() {
        if (getVersion() != Shared.VERSION)
            return -1;

        return ((content[4] & 0xFF) << 8) | (content[5] & 0xFF);
    }

    /**
     * Extract version field as a hex char from raw UUID bytes. By default, generated UUIDs will have 'b' as the
     * version, but it is possible to parse UUIDs of different types, '4' for example.
     * @return UUID version as a char.
     */
    public char getVersion() {
        return HEX[(content[6] & 0xF0) >> 4];
    }

    /**
     * Extract timestamp from raw UUID bytes and return as int. If the UUID is not the default type, then we can't parse
     * the timestamp out and null is returned.
     * @return Millisecond UTC timestamp from generation of the UUID, or null for unrecognized format.
     */
    public Instant getTimestamp() {
        if (getVersion() != Shared.VERSION)
            return null;

        long time;
        time  = ((long)content[10] & 0xFF) << 40;
        time |= ((long)content[11] & 0xFF) << 32;
        time |= ((long)content[12] & 0xFF) << 24;
        time |= ((long)content[13] & 0xFF) << 16;
        time |= ((long)content[14] & 0xFF) << 8;
        time |= ((long)content[15] & 0xFF);
        return Instant.ofEpochMilli(time);
    }
}