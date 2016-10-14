A little experiment replicating cassandra stress but 
using execute async and a thread per core.

Goal of this project is to create a load test tool for cassandra that has
no contention/blocking and very little allocation.

### Building

```
./gradlew shadowJar
```

### Running

```
./bin/cloud.sh
```

## Minimum viable product

Type support:

* Text
* 32 bit ints
* 64 bit ints

Scenarios:

* Ratio of different queries

Data generation:

* Size of text - fixed, normal, exponential
* Number of columns - single, fixed number

