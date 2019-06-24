
## Open Sky

https://opensky-network.org

Terms of Use

https://opensky-network.org/about/terms-of-use


## Build

gradle clean build shadowJar
rm -f build/libs/opensky-source-connector.jar

## Standalone (produce Avro)

connect-standalone config/worker.properties config/connect-standalone.properties

## Standalone (producer JSON)

cd config
connect-standalone config/worker.properties config/connect-standalone-json.properties
