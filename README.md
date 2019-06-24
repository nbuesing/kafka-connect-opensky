
#

The opensky API library is GPLv3, I am in the process of writing my own Feign based RESTful call to their endpoints.

The use of the connector has to follow the OpenSky terms and conditions for usage of their API, but the connector with the
modification will be able to be under the Apache 2 licensing correctly.

# OpenSky Source Connector

The OpenSky Source Connector provides a way to inject OpenSky Network into an Apache Kafka Cluster.

## Open Sky

"a community-based receiver network which continuously collects air traffic surveillance data." -- https://opensky-network.org

Prior to using Open Sky data, please check the [Terms Of Use](https://opensky-network.org/about/terms-of-use).

## Build

To build the project, use `gradlew`. The build.gradle file leverages a shadow jar plugin to build the uber jar that makes it easier
to deploy the connector plugin. The uber jar is placed in `build/connect` to keep it isolated from the non-uber jar to make it easier
to test with `standalone-connect`. 

```
./gradlew clean build
```

## Example Use Cases

The example connection showcases reading from the OpenSky API and write it into a Kafka topic as Avro. Please update the 
`bootstrap.servers` to reflect the broker(s) you are using as well as any other broker connection settings.

### Standalone 

The following standalone connector will run the connector on your local machine.

```
connect-standalone config/worker.properties config/connect-standalone.properties
```

## Concerns / Issues

* Using multiple bounding boxes causes issues with the the frequency of calling into OpenSky API. Working on how to allow for more
than one worker tasks to actually run.

* Unit tests and integration tests. Unit tests exist for the utility and conversion code, but not for the task and connector.
Need to get those completed.

* Integration tests, those are needed as well.

* Confirmation of data structures.

* Additional validations with JSON, csv, and Avro.
