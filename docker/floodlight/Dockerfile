FROM openjdk:8

USER root
WORKDIR /root

COPY setup.sh /

RUN sh /setup.sh

EXPOSE 6633 6653 6640 6650

ENTRYPOINT ["bash"]
