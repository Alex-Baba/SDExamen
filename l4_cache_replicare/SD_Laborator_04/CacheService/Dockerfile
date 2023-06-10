FROM openjdk:8-jdk-alpine
ADD target/CacheService-1.0-SNAPSHOT.jar CacheService.jar
ADD CacheDB.txt CacheDB.txt
ENTRYPOINT ["java","-jar", "CacheService.jar"]