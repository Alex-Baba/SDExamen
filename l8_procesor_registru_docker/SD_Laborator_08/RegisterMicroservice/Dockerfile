FROM openjdk:8-jdk-alpine
ADD RegisterMicroservice/target/RegisterMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar RegisterMicroservice.jar

ENTRYPOINT ["java","-jar", "RegisterMicroservice.jar"]