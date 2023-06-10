array=($(docker ps -q))
id=`docker ps -aqf "name=etcd-gcr-v3.4.7"`

for t in ${array[@]}; do
    #echo $t
    if [ "$id" != $t ]; then
        docker stop $t
    fi
done

array=($(docker ps -a -q))
for t in ${array[@]}; do
    #echo $t
    if [ "$id" != $t ]; then
        docker rm -f $t
    fi
done

docker rmi $(docker images -q)