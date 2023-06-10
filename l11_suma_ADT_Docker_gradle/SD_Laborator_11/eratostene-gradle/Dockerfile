FROM openjdk:8-jdk-alpine

ADD ./build/libs/eratostene-gradle-0.1-all.jar sumservice.jar

ENTRYPOINT ["java","-jar","sumservice.jar"]