FROM confluentinc/cp-kafka-connect:7.5.1

ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components"

COPY build/distributions/kafka-connect-opensky.zip /hub-downloads/

USER root

RUN \
    mkdir -p /spooldir/input && \
    mkdir -p /spooldir/error && \
    mkdir -p /spooldir/finished

#COPY airlines.csv /spooldir/input

RUN \
    confluent-hub install --no-prompt /hub-downloads/kafka-connect-opensky.zip && \
    confluent-hub install --no-prompt jcustenborder/kafka-connect-spooldir:latest

USER appuser
