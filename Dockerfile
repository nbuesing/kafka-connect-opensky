FROM confluentinc/cp-kafka-connect:5.3.0

ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components"

COPY build/distributions/nbuesing-kafka-connect-opensky*.zip /hub-downloads/kafka-connect-opensky.zip

RUN confluent-hub install --no-prompt /hub-downloads/kafka-connect-opensky.zip

