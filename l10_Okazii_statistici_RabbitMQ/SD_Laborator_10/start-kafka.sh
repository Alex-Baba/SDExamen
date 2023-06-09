docker run -d --restart=always -p 2379:2379 -p 2380:2380 --mount type=bind,source=/var/etcd-data.tmp,destination=/var/etcd-data --name etcd-gcr-v3.4.7 gcr.io/etcd-development/etcd:v3.4.7 \
/usr/local/bin/etcd --name s1 --data-dir /var/etcd-data --listen-client-urls http://0.0.0.0:2379 --advertise-client-urls \
http://0.0.0.0:2379 --listen-peer-urls http://0.0.0.0:2380 --initial-advertise-peer-urls http://0.0.0.0:2380 --initial-cluster \
s1=http://0.0.0.0:2380 --initial-cluster-token tkn --initial-cluster-state new --log-level info --logger zap --log-outputs stderr 

sudo -E /opt/kafka/bin/kafka-server-start.sh -daemon \
/opt/kafka/config/server.properties

ps -ef | grep kafka