FROM confluentinc/cp-kafka-connect:5.3.0

ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components"

COPY build/distributions/nbuesing-opensky-source-connector.zip /hub-downloads/nbuesing-opensky-source-connector.zip

RUN \
    rm -fr /usr/share/java/kafka-connect-activemq && \
    rm -fr /usr/share/java/kafka-connect-elasticsearch && \
    rm -fr /usr/share/java/kafka-connect-ibmmq && \
    rm -fr /usr/share/java/kafka-connect-jdbc && \
    rm -fr /usr/share/java/kafka-connect-jms && \
    rm -fr /usr/share/java/kafka-connect-s3 && \
    confluent-hub install --no-prompt /hub-downloads/nbuesing-opensky-source-connector.zip

