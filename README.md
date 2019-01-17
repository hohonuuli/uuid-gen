# uuid-gen

Convience API for generating various types of UUIDs that I use for my projects. To use just import `UUIDs` and call it's static methods:

```java
import org.mbari.uuid.UUIDs;
UUID uuid = UUIDs.uuid1();
```

## Table of Contents

1. [UUID Types](#uuid-types)
    1. [UUID 1](#uuid-1)
    2. [UUID 4](#uuid-4)
    3. [UUID 6](#uuid-6)
    4. [UUID Counters](#uuid-sequence-and-uuid-counter)
2. [Maven](#maven)

## UUID Types

### UUID 1

[Based on date-time and MAC address](https://tools.ietf.org/html/rfc4122#section-4.2.1). This implementation uses [java-uuid-generator](https://github.com/cowtowncoder/java-uuid-generator)

```java
UUID uuid1 = UUIDs.uuid1();
``` 
Examples:
```
35df41db-19f0-11e9-bf67-be655249e714
35df41dc-19f0-11e9-bf67-be655249e714
35df41dd-19f0-11e9-bf67-be655249e714
35df41de-19f0-11e9-bf67-be655249e714
```

### UUID 4

Random UUID. Same as `java.util.UUID.randomUuid();`

```java
UUID uuid4 = UUIDs.uuid4();
```

Examples:

```
bf858504-7777-46c0-9291-75d2ab675569
bd6b0fb7-70ea-4acf-be58-8d64e3a183bc
a273306c-676f-4dc5-a92b-d25875539474
d9495e93-24ae-453b-818a-f52459f1cd69
```

### UUID 6

Not an actual version, but an interesting proposal from <https://bradleypeabody.github.io/uuidv6/>. It has a nice feature in that it's a variant of UUID 1. So it's based on date-time and MAC address but also has a natural sort order. For example, if you convert it to a string it sorts by time correctly.

```java
UUID uuid6 = UUIDs.uuid6();
```

Examples:

```
1e919f03-5efe-63af-bf67-be655249e714
1e919f03-5efe-63b0-bf67-be655249e714
1e919f03-5efe-63b1-bf67-be655249e714
1e919f03-5efe-63b2-bf67-be655249e714
```


### UUID Sequence and UUID Counter

Locality-based UUID algorithmns created by [Groupon](https://github.com/groupon/locality-uuid.java).

They generated UUIDs in the following format:

```java
   wwwwwwww-xxxx-byyy-yyyy-zzzzzzzzzzzz

w: counter value
x: process id
b: literal hex 'b' representing the UUID version
y: fragment of machine MAC address
z: UTC timestamp (milliseconds since epoch)
```

For Example:

```java
   20be0ffc-314a-bd53-7a50-013a65ca76d2

counter     : 3,488,672,514
process id  : 12,618
MAC address : __:__:_d:53:7a:50
timestamp   : 1,350,327,498,450 (Mon, 15 Oct 2012 18:58:18.450 UTC)
```

__UUID Counter__:

```java
UUID uuidC = UUIDs.uuidCounter();
```

Example UUIDs. Note that the counter is incremented by 1

```
dc58fb13-f6ac-b249-e714-0168593ebabc
dc58fb14-f6ac-b249-e714-0168593ebb16
dc58fb15-f6ac-b249-e714-0168593ebb16
dc58fb16-f6ac-b249-e714-0168593ebb16
```

__UUID Sequence__:

```java
UUID uuidS = UUIDs.uuidSequence();
```

Example UUIDs. Note that the counter increments by a large prime number.

```
33b06446-f6ac-b249-e714-0168593ebb16
8e7ca107-f6ac-b249-e714-0168593ebb16
d948feb7-f6ac-b249-e714-0168593ebb17
25144cf8-f6ac-b249-e714-0168593ebb17
```

__Decomposing Counter and Sequence UUIDs__:

If desired you can decompose these UUIDs into their components like so:

```java
import java.time.Instant;
import org.mbari.uuid.sequence.DecomposedUUID;

//...
UUID uuidS = UUIDS.uuidSequence();
DecomposedUUID d = new DecomposedUUID(uuidS);

int     processId   = d.getProcessId();
byte[]  macFragment = d.getMacFracment();
Instant timestamp   = d.getTimestamp()
char    version     = d.getVersion();
```

## Maven

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
     <groupId>org.mbari.uuid</groupId>
      <artifactId>uuid-gen</artifactId>
      <version>0.1.1</version>
    </dependency>
</dependencies>

```


## SQL Server _uniqueidentifier_

 If you're working with SQL Server, I need a good external UUID generator for using UUIDs as a primary key, [if you need to do such a thing](https://stackoverflow.com/questions/11938044/what-are-the-best-practices-for-using-a-guid-as-a-primary-key-specifically-rega).  SQL Server's has a very [odd sorting order](http://sqlblog.com/blogs/alberto_ferrari/archive/2007/08/31/how-are-guids-sorted-by-sql-server.aspx) which is:

```
    wwwwwwww-xxxx-byyy-yyyy-zzzzzzzzzzzz
    0 1 2 3  4 5  6 7  8 9  A B C D E F
```

- __0..3__ are evaluated in left to right order and are the less important, then
- __4..5__ are evaluated in left to right order, then
- __6..7__ are evaluated in left to right order, then
- __8..9__ are evaluated in right to left order, then
- __A..F__ are evaluated in right to left order and are the most important
