FROM openjdk:8-jdk-alpine
ENV DOCKERVERSION=19.03.12
RUN apk add curl && apk add tar
RUN curl -fsSLO https://download.docker.com/linux/static/stable/x86_64/docker-${DOCKERVERSION}.tgz \
  && tar xzvf docker-${DOCKERVERSION}.tgz --strip 1 -C /usr/local/bin docker/docker \
  && rm docker-${DOCKERVERSION}.tgz
ADD ./ReplicatorMicroservice/target/ReplicatorMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar ReplicatorMicroservice.jar
ENTRYPOINT ["java","-jar", "ReplicatorMicroservice.jar"]