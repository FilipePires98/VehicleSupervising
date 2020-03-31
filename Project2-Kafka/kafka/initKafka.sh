if [ "$#" -le 0 ]
then
  echo "Pass the name of the topics!"
  exit 1
fi

rm pids

if [ ! -d "kafkaSrc" ]
then
    mkdir kafkaSrc
fi
cd kafkaSrc

if [ ! -d "kafka_2.12-2.4.1" ]
then
    wget https://mirrors.up.pt/pub/apache/kafka/2.4.1/kafka_2.12-2.4.1.tgz
    tar -xzf kafka_2.12-2.4.1.tgz
    rm kafka_2.12-2.4.1.tgz
fi
cd kafka_2.12-2.4.1

bin/zookeeper-server-start.sh ../../configs/zookeeper.properties 1>/dev/null &
echo $! >> ../../pids

bin/kafka-server-start.sh ../../configs/config1.properties 1>/dev/null &
echo $! >> ../../pids

bin/kafka-server-start.sh ../../configs/config2.properties 1>/dev/null &
echo $! >> ../../pids

bin/kafka-server-start.sh ../../configs/config3.properties 1>/dev/null &
echo $! >> ../../pids

for var in "$@"
do

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 1 --topic $var

done