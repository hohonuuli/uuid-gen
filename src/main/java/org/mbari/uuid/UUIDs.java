package org.mbari.uuid;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import java.util.UUID;


/**
 * UUIDs
 */
public class UUIDs {

  private static final TimeBasedGenerator timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());

  public static UUID uuid4() {
    return UUID.randomUUID();
  }

  public static UUID uuid1() {
    return timeBasedGenerator.generate();
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
   * @return
   */
  public static UUID uuid6(UUID uuid1) {
    long ut = uuid1.getMostSignificantBits();
    long ut2 = ((ut >> 32) & 0x0FFF) | // 12 least significant bits
            (0x6000) | // version number
            ((ut >> 28) & 0x0000000FFFFF0000L) | // next 20 bits
            ((ut << 20) & 0x000FFFF000000000L) | // next 16 bits
            (ut << 52); // 12 most significant bits

    return new UUID(ut2, uuid1.getLeastSignificantBits());
  }




}
