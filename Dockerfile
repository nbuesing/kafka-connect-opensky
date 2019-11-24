FROM confluentinc/cp-kafka-connect:5.3.1

ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components"

#COPY build/distributions/nbuesing-opensky-source-connector.zip /hub-downloads/nbuesing-opensky-source-connector.zip

COPY build/distributions/nbuesing-opensky-source-connector.zip /hub-downloads/
#COPY build/distributions/jcustenborder-kafka-connect-spooldir-1.0.41.zip /hub/downloads

RUN \
    mkdir -p /spooldir/input && \
    mkdir -p /spooldir/error && \
    mkdir -p /spooldir/finished

COPY airlines.csv /spooldir/input

#    rm -fr /usr/share/java/kafka-connect-elasticsearch && \

RUN \
    rm -fr /usr/share/java/kafka-connect-activemq && \
    rm -fr /usr/share/java/kafka-connect-ibmmq && \
    rm -fr /usr/share/java/kafka-connect-jdbc && \
    rm -fr /usr/share/java/kafka-connect-jms && \
    rm -fr /usr/share/java/kafka-connect-s3 && \
    confluent-hub install --no-prompt /hub-downloads/nbuesing-opensky-source-connector.zip && \
    confluent-hub install --no-prompt jcustenborder/kafka-connect-spooldir:latest

#    confluent-hub install --no-prompt /hub-downloads/jcustenborder-kafka-connect-spooldir-1.0.41.zip

