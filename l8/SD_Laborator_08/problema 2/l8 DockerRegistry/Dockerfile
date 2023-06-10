FROM openjdk:8-jdk-alpine
ADD build/libs/DockerRegistry-1.0.0.jar DockerRegistry.jar

ENTRYPOINT ["java","-jar", "DockerRegistry.jar"]