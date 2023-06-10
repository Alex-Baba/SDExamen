FROM openjdk:8-jdk-alpine
ADD StudentMicroservice/target/StudentMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar StudentMicroservice.jar
ADD StudentMicroservice/questions_database.txt questions_database.txt

ENTRYPOINT ["java","-jar", "StudentMicroservice.jar"]