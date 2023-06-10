docker run -d -p 1700:1700 --name heartbeat_microservice --network=ms-net img_heartbeat_microservice:v1
sleep 5
docker run -d -p 1500:1500 -e HEARTBEAT_HOST='heartbeat_microservice' --name message_manager --network=ms-net img_message_manager_microservice:v1
sleep 5
docker run -d -e HEARTBEAT_HOST='heartbeat_microservice' -e MESSAGE_MANAGER_HOST='message_manager' --name catalog_microservice --network=ms-net img_catalog_microservice:v1
sleep 5
docker run -d -p 1600:1600 -e MESSAGE_MANAGER_HOST='message_manager' -e HEARTBEAT_HOST='heartbeat_microservice' --name teacher_microservice --network=ms-net img_teacher_microservice:v1
sleep 5
for student in $( seq 1 $1 )
do
    docker run -d -e HEARTBEAT_HOST='heartbeat_microservice' -e MESSAGE_MANAGER_HOST='message_manager' --name student_microservice_$student --network=ms-net img_student_microservice:tip$student
    sleep 5
done
sleep 2
docker run -d -e HEARTBEAT_HOST='heartbeat_microservice' -e MESSAGE_MANAGER_HOST='message_manager' --name assistant_microservice --network=ms-net img_assistant_microservice:v1
docker ps