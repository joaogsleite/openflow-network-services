FROM ubuntu:16.04

USER root
WORKDIR /root

COPY entrypoint.sh /
COPY setup.sh /

RUN sh /setup.sh

EXPOSE 6633 6653 6640 6650

ENTRYPOINT ["/entrypoint.sh"]
