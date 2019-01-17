# uuid-gen

Simple API for generating various types of UUIDs. 

## Table of Contents

1. [Usage](#usage)
    - [Maven](#maven)
2. [UUID Types](#uuid-types)
    - [UUID 1](#uuid-1)
    - [UUID 4](#uuid-4)
    - [UUID 6](#uuid-6)
    - [COMB](#comb)
    - [UUID Counters](#uuid-sequence-and-uuid-counter)
2. [SQL Server](#sql-server-uniqueidentifier)

## Usage

To use, just import `UUIDs` and call its static methods:

```java
import org.mbari.uuid.UUIDs;
UUID uuid = UUIDs.uuid1();
```

### Maven

Here's the Maven xml needed to include it in your project.

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
      <version>0.1.3</version>
    </dependency>
</dependencies>

```

## UUID Types

### UUID 1

```java
UUID uuid1 = UUIDs.uuid1();
``` 

[Based on date-time and MAC address](https://tools.ietf.org/html/rfc4122#section-4.2.1). This implementation uses [java-uuid-generator](https://github.com/cowtowncoder/java-uuid-generator).

Examples:
```
35df41db-19f0-11e9-bf67-be655249e714
35df41dc-19f0-11e9-bf67-be655249e714
35df41dd-19f0-11e9-bf67-be655249e714
35df41de-19f0-11e9-bf67-be655249e714
```

### UUID 4

```java
UUID uuid4 = UUIDs.uuid4();
```

Random UUID. Same as `java.util.UUID.randomUuid();`

```
    rrrrrrrr-rrrr-4rrr-rrrr-rrrrrrrrrrrr
      
 r: random value
 4: version (type 4 is random)
```



Examples:

```
bf858504-7777-46c0-9291-75d2ab675569
bd6b0fb7-70ea-4acf-be58-8d64e3a183bc
a273306c-676f-4dc5-a92b-d25875539474
d9495e93-24ae-453b-818a-f52459f1cd69
```

### UUID 6

```java
UUID uuid6 = UUIDs.uuid6();
```

Not an actual version, but an interesting proposal from <https://bradleypeabody.github.io/uuidv6/>. It has a nice feature in that it's a variant of UUID 1. So it's based on date-time and MAC address but also has a natural sort order. For example, if you convert it to a string it sorts by time correctly.



Examples:

```
1e919f03-5efe-63af-bf67-be655249e714
1e919f03-5efe-63b0-bf67-be655249e714
1e919f03-5efe-63b1-bf67-be655249e714
1e919f03-5efe-63b2-bf67-be655249e714
```

### COMB

```java
UUID comb = UUIDs.comb();
```

This is a UUID that [combines random values for the most significant bits and a timestamp in the least significant bits](http://www.informit.com/articles/article.aspx?p=25862&seqNum=7). This was shown to give better insert performance for SQL Server as it satisfies the [weird sort order used by SQL Server](#sql-server-uniqueidentifier) for unique ids. The generated format is:

```
     rrrrrrrr-rrrr-4rrr-tt6t-tttttttttttt
     0                  8              F
      
 r: random value
 4: version (type 4 is random)
 t: UTC timestamp (+counter). The byte order is inverted so the least varying 
    byte is at position F and the most varying is at 8.
 6: I'm lazy. This is the version number from UUID4 which I'm using to generate
    the time. 

```

Examples:

```
f298fa80-6af8-43d4-5467-26fe52a8911e
b9a7aafe-129a-466a-656e-28fe52a8911e
c57df92f-41f6-4fcd-666e-28fe52a8911e
b4d0c556-3afa-40ad-676e-28fe52a8911e
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

## SQL Server uniqueidentifier

 If you're working with SQL Server, be careful when using UUIDS as primary keys ... [if you need to do such a thing](https://stackoverflow.com/questions/11938044/what-are-the-best-practices-for-using-a-guid-as-a-primary-key-specifically-rega).  SQL Server has a very [odd sorting order](http://sqlblog.com/blogs/alberto_ferrari/archive/2007/08/31/how-are-guids-sorted-by-sql-server.aspx) which is:

```
    wwwwwwww-xxxx-byyy-yyyy-zzzzzzzzzzzz
    0 1 2 3  4 5  6 7  8 9  A B C D E F
```

- __0..3__ are evaluated in left to right order and are the less important, then
- __4..5__ are evaluated in left to right order, then
- __6..7__ are evaluated in left to right order, then
- __8..9__ are evaluated in right to left order, then
- __A..F__ are evaluated in right to left order and are the most important

So ... the byte evaluation order for sorting is:

```
F E D C B A 9 8 6 7 4 5 0 1 2 3
```

The [COMB](#comb) UUID should satisfy this sort ordering.
