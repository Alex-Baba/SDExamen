function build_student {
    echo "building for Student$1"
    cp question_database$1.txt StudentMicroservice/questions_database.txt
    cd StudentMicroservice/
    docker build -t img_student_microservice:tip$1 .
    rm questions_database.txt
    cd ..
}

function build_teacher {
    cd TeacherMicroservice
    docker build -t img_teacher_microservice:v1 .
    cd ..
}

function build_manager {
    cd MessageManagerMicroservice
    docker build -t img_message_manager_microservice:v1 .
    cd ..
}

function build_heartbeat {
    cd HeartbeatMicroservice
    docker build -t img_heartbeat_microservice:v1 .
    cd ..
}

function build_registry {
    cd DockerRegistry
    docker build -t img_registry_microservice:v1 .
    cd ..
}

function build_assistant {
    cd AssistantMicroservice
    docker build -t img_assistant_microservice:v1 .
    cd ..
}


function build_catalog {
    cd CatalogService
    docker build -t img_catalog_microservice:v1 .
    cd ..
}

for student_nr in $( seq 1 $1 )
do
    build_student $student_nr
done
build_teacher
build_manager
build_heartbeat
build_registry
build_assistant
build_catalog

docker images