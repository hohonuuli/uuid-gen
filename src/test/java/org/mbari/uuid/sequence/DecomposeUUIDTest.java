package org.mbari.uuid.sequence;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mbari.uuid.UUIDs;
import static org.junit.jupiter.api.Assertions.*;

public class DecomposeUUIDTest {

    @Test
    public void testDecomposeCounterUUID() {
        var uuid = UUIDs.uuidCounter();
        var d = new DecomposedUUID(uuid);

        var dt = d.getTimestamp().getEpochSecond() - Instant.now().getEpochSecond();
        assertTrue(dt < 1L);
        assertEquals(Shared.VERSION, d.getVersion());
        assertEquals(Shared.PID, d.getProcessId());
        var mac = d.getMacFragment();
        assertEquals(Shared.MAC[3], mac[3]);
        assertEquals(Shared.MAC[4], mac[4]);
        assertEquals(Shared.MAC[5], mac[5]);

    }

    @Test
    public void testDecomposeSequenceUUID() {
        var uuid = UUIDs.uuidSequence();
        var d = new DecomposedUUID(uuid);

        var dt = d.getTimestamp().getEpochSecond() - Instant.now().getEpochSecond();
        assertTrue(dt < 1L);
        assertEquals(Shared.VERSION, d.getVersion());
        assertEquals(Shared.PID, d.getProcessId());
        var mac = d.getMacFragment();
        assertEquals(Shared.MAC[3], mac[3]);
        assertEquals(Shared.MAC[4], mac[4]);
        assertEquals(Shared.MAC[5], mac[5]);
    }
    
}
