function push {
    if [[ "$#" -ne 1 ]]; then
        echo "Bad number of arguments!"
        return -1
    fi
    detalii_img=`docker images | grep $1`
    array_detalii=($detalii_img)
    json="{\"imageId\":\"${array_detalii[2]}\",\"name\":\"${array_detalii[0]}\",\"tag\":\"${array_detalii[1]}\",\"space\":\"${array_detalii[6]}\"}"
    echo $json
    curl -X POST localhost:8000/registry -H 'Content-Type: application/json' -d $json
}

function rm {
    if [[ "$#" -ne 1 ]]; then
        echo "Bad number of arguments!"
        return -1
    fi
    `curl -X "DELETE" localhost:8000/registry/$1`
}

function ls {
    if [[ "$#" -eq 0 ]]; then
        lista=`curl -X GET localhost:8000/registry`
        echo $lista
    elif [[ "$#" -eq 1 ]]; then
        img=`curl -X GET localhost:8000/registry/$1`
        echo $img
    elif [[ "$#" -eq 2 ]]; then
        if [[ "$1" = "--name" ]]; then
            img=`curl -X GET localhost:8000/registry/name/$2`
            echo $img
        else
            img=`curl -X GET localhost:8000/registry/$1/$2`
            echo $img
        fi
    else
        echo "Bad number of arguments!"
    fi
}

if [[ "$#" -gt 3 ]]; then
    echo "Bad number of arguments!"
    exit -1
fi
$1 $2 $3