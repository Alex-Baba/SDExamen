
#Message manager

docker build -t localhost:5000/message_manager_microservice:v1 .
docker push localhost:5000/student_microservice:tip1

docker run -d -p 1500:1500 --name message_manager --network=ms-net localhost:5000/message_manager_microservice:v1


#student microservice

docker build -t localhost:5000/student_microservice:tip1 .
docker push localhost:5000/student_microservice:tip1

docker run -d -e MESSAGE_MANAGER_HOST='message_manager' --name student_microservice_1 --network=ms-net localhost:5000/student_microservice:tip1


#Teacher Manager

docker build -t localhost:5000/teacher_manager_microservice:v1 .
docker push localhost:5000/teacher_manager_microservice:v1 .

docker run -d -p 1700:1700 -e MESSAGE_MANAGER_HOST='message_manager' --name teacher_manager --network=ms-net localhost:5000/teacher_manager_microservice:v1

#Teacher microservice

docker build -t localhost:5000/teacher_microservice:v1 .
docker push localhost:5000/message_manager_microservice:v1

docker run -d -p 1600:1600 -e TEACHER_MANAGER_HOST='teacher_manager' --name teacher_microservice --network=ms-net localhost:5000/teacher_microservice:v1

