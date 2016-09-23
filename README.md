A little experiment replicating cassandra stress but 
using execute async and a thread per core.

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
* 64 bit logs

Scenarios:

* Ratio of differnt queries

Data generation:

* Size of text - fixed, normal, exponential
* Number of columns - single, fixed number

