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

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Shared {

    public static final int PID = processId();
    public static final byte[] MAC = macAddress();
    private static final int MAX_PID = 65536;
    protected static final char VERSION = 'b';
    protected static final int VERSION_DEC = mapToByte(VERSION, '0');

    /**
     * Get the process id of this JVM. I haven't tested this extensively so its possible that this performs differently
     * on esoteric JVMs. I copied this from:
     * http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
     * @return Id of the current JVM process.
     */
    private static int processId() {
        // Note: may fail in some JVM implementations
        // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        if (index < 1)
            throw new RuntimeException("Could not get PID");

        try {
            return Integer.parseInt(jvmName.substring(0, index)) % MAX_PID;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Could not get PID");
        }
    }

    /**
     * Get the active MAC address on the current machine as a byte array. This is called when generating a new UUID.
     * Note that a machine can have multiple or no active MAC addresses. This method works by iterating through the list
     * of network interfaces, ignoring the loopback interface and any virtual interfaces (which often have made-up
     * addresses), and returning the first one we find. If no valid addresses are found, then a byte array of the same
     * length with all zeros is returned.
     * @return 6-byte array for first active MAC address, or 6-byte zeroed array if no interfaces are active.
     */
    private static byte[] macAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            byte[] mac = null;

            while (interfaces.hasMoreElements() && (mac == null || mac.length != 6)) {
                NetworkInterface netInterface = interfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual())
                    continue;
                mac = netInterface.getHardwareAddress();
            }

            // if the machine is not connected to a network it has no active MAC address
            if (mac == null)
                mac = new byte[] {0, 0, 0, 0, 0, 0};

            return mac;
        } catch (Exception e) {
            throw new RuntimeException("Could not get MAC address");
        }
    }

    /**
     * Map two hex characters to 4-bit numbers and combine them to produce 8-bit number in byte.
     * @param a First hex character.
     * @param b Second hex character.
     * @return Byte representation of given hex characters.
     */
    protected static byte mapToByte(char a, char b) {
        int ai = intValue(a);
        int bi = intValue(b);
        return (byte) ((ai << 4) | bi);
    }

    /**
     * This method maps a hex character to its 4-bit representation in an int.
     * @param x Hex character in the range ('0' - '9', 'a' - 'f', 'A' - 'F').
     * @return 4-bit number in int representing hex offset from 0.
     */
    private static int intValue(char x) {
        if (x >= '0' && x <= '9')
            return x - '0';
        if (x >= 'a' && x <= 'f')
            return x - 'a' + 10;
        if (x >= 'A' && x <= 'F')
            return x - 'A' + 10;
        throw new RuntimeException("Error parsing UUID at character: " + x);
    }

    /**
     * Get the most significant bits (the first half) of the UUID content as a 64-bit long.
     * @return The first half of the UUID as a long.
     */
    protected static long getMostSignificantBits(byte[] content) {
        long a;
        a  = ((long)content[ 0] & 0xFF) << 56;
        a |= ((long)content[ 1] & 0xFF) << 48;
        a |= ((long)content[ 2] & 0xFF) << 40;
        a |= ((long)content[ 3] & 0xFF) << 32;
        a |= ((long)content[ 4] & 0xFF) << 24;
        a |= ((long)content[ 5] & 0xFF) << 16;
        a |= ((long)content[ 6] & 0xFF) << 8;
        a |= ((long)content[ 7] & 0xFF);
        return a;
    }

    /**
     * Get the least significant bits (the second half) of the UUID content as a 64-bit long.
     * @return The second half of the UUID as a long.
     */
    protected static long getLeastSignificantBits(byte[] content) {
        long b;
        b  = ((long)content[ 8] & 0xFF) << 56;
        b |= ((long)content[ 9] & 0xFF) << 48;
        b |= ((long)content[10] & 0xFF) << 40;
        b |= ((long)content[11] & 0xFF) << 32;
        b |= ((long)content[12] & 0xFF) << 24;
        b |= ((long)content[13] & 0xFF) << 16;
        b |= ((long)content[14] & 0xFF) << 8;
        b |= ((long)content[15] & 0xFF);
        return b;
    }



}
