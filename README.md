# uuid-gen

Convience API for generating various types of UUIDs that I use for my projects. The API is:

```java
import java.util.UUID;
import org.mbari.uuid.UUIDs;

// (date-time and MAC address). Uses https://github.com/cowtowncoder/java-uuid-generator
UUID uuid1 = UUIDs.uuid1()

// Basically UUID.randomUuid()
UUID uuid4 = UUIDs.uuid4()

// https://bradleypeabody.github.io/uuidv6/
UUID uuid6 = UUIDs.uuid6()

// A locality based UUID from groupon. ID's are very sequential
// https://github.com/hohonuuli/locality-uuid.java
UUID uuidS = UUIDs.sequenceUuid()

// A locality based UUID from groupon. ID's are very sequential. Good natural sort order
// https://github.com/hohonuuli/locality-uuid.java
UUID uuidC = UUIDs.counterUuid()

```

### Maven

```xml
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-hohonuuli-maven</id>
        <name>bintray</name>
        <url>https://dl.bintray.com/hohonuuli/maven</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
     <groupId>org.mbari.bobd</groupId>
      <artifactId>uuid-gen</artifactId>
      <version>0.1</version>
    </dependency>
</dependencies>

```
