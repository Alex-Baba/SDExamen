FROM openjdk:8-jdk-alpine
ADD ./TeacherMicroservice/target/TeacherMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar TeacherMicroservice.jar

ENTRYPOINT ["java","-jar", "TeacherMicroservice.jar"]