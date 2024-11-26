package org.mbari.uuid;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UUIDsTest {

    @Test
    public void testUUID1ToUUID6() {
        var uuid1 = UUID.fromString("37f9f6ce-190a-11e9-9d6a-0242ac1c0002");
        var uuid6 = UUIDs.uuid6(uuid1);
        var uuid6String = uuid6.toString();
        assertEquals("1e9190a3-7f9f-66ce-9d6a-0242ac1c0002", uuid6String);
    }

}