/**
 * This is a UUID class intended to help control data locality when inserting into a distributed data system, such as MongoDB or HBase. There is also a Ruby implementation. This version does not conform to any external standard or spec. Developed at Groupon in Palo Alto by Peter Bakkum and Michael Craig.
 * <div>
 * Problems we encountered with other UUID solutions:
 * <ul>
 *     <li>Collisions when used with heavy concurrency.</li>
 *     <li>Difficult to retrieve useful information, such as the timetamp, from the id.</li>
 *     <li>Time-based versions (such as v1) place this component at the front, meaning all generated ids start with the same bytes for some time period. This was disadvantageous for us because all of these writes were hashed to the same shards of our distributed databases. Thus, only one machine was receiving writes at a given time.</li>
 * </ul>
 * </div>
 * <div>
 * Solutions:
 * <ul>
 *     <li>We have tested thoroughly in a concurrent environment and include both the PID and MAC Address in the UUID. In the first block of the UUID we have a counter which we increment with a large primary number, ensuring that the counter in a single process takes a long time to wrap around to the same value.</li>
 *     <li>The UUID layout is very simple and documented below. Retrieving the millisecond-precision timestamp is as simple as copying the last segment and converting from hex to decimal.</li>
 *     <li>o ensure that generated ids are evenly distributed in terms of the content of their first few bytes, we increment this value with a large number. This means that DB writes using these values are evenly distributed across the cluster. We ALSO allow toggling into sequential mode, so that the first few bytes of the UUID are consistent and writes done with these keys hash to tho same machine in the cluster, when this characteristic is desireable. Thus, this library is a tool for managing the locality of reads and writes of data inserted with UUID keys.</li>
 * </ul>
 * </div>
 * <h2>NOTES</h2>
 * <div>This UUID version was designed to have easily readable PID, MAC address, and timestamp values, with a regularly incremented count. The motivations for this implementation are to reduce the chance of duplicate ids, store more useful information in UUIDs, and ensure that the first few characters vary for successively generated ids, which can be important for splitting ids over a cluster. The UUID generator is also designed to be be thread-safe without locking.</div>
 * <div>Uniqueness is supported by the millisecond precision timestamp, the MAC address of the generating machine, the 2 byte process id, and a 4 byte counter. Thus, a UUID is guaranteed to be unique in an id space if each machine allows 65,536 processes or less, does not share the last 28 bits of its MAC address with another machine in the id space, and generates fewer than 4,294,967,295 ids per millisecond in a process.</div>
 * <div><b>Counter:</b> The counter value is reversed, such that the least significant 4-bit block is the first character of the UUID. This is useful because it makes the earlier bits of the UUID change more often. Note that the counter is not incremented by 1 each time, but rather by a large prime number, such that its incremental value is significantly different, but it takes many iterations to reach the same value.</div>
 * <div>Examples of sequentially generated ids in the default counter mod</div>
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
 * <div>Note the high variability of the first few characters.</div>
 * <div>The counter can also be toggled into sequential mode to effectively reverse this logic. This is useful because it means you can control the locality of your data as you generate ids across a cluster. Sequential mode works by creating an initial value based on a hash of the current date and hour. This means it can be discovered independently on distributed machines. The value is then incremented by one for each id generated. If you use key-based sharding, data inserted with these ids should have some locality.</div>
 * <div>Examples of sequentially generated ids in sequential counter mode:</div>
 * <pre>
 * f5166777-7a7f-bd53-7a50-013e4e2afc26
 * f5166778-7a7f-bd53-7a50-013e4e2afc26
 * f5166779-7a7f-bd53-7a50-013e4e2afc26
 * f516677a-7a7f-bd53-7a50-013e4e2afc26
 * f516677b-7a7f-bd53-7a50-013e4e2afc26
 * f516677c-7a7f-bd53-7a50-013e4e2afc26
 * f516677d-7a7f-bd53-7a50-013e4e2afc26
 * f516677e-7a7f-bd53-7a50-013e4e2afc26
 * </pre>
 * <div>
 *     <ul>
 *         <li>PID: This value is just the current process id modulo 65,536. In my experience, most linux machines do not allow PID numbers to go this high, but OSX machines do.</li>
 *         <li>MAC Address: The last 28 bits of the first active MAC address found on the machine. If no active MAC address is found, this is filled in with zeroes.</li>
 *         <li>Timestamp: This is the UTC milliseconds since Unix epoch. To convert to a time manually first copy the last segment of the UUID, convert to decimal, then use a time library to count up from 1970-1-1 0:00:00.000 UTC.</li>
 *     </ul>
 * </div>
 * @author Brian Schlining
 * @since 2019-01-15T15:23:00
 */
package org.mbari.uuid.sequence;