./deleteKafka.sh

if [ "$#" -le 0 ]
then
  echo "Pass the name of the topics!"
  exit 1
fi

rm pids

../../kafka_2.12-2.4.1/bin/zookeeper-server-start.sh configs/zookeeper.properties 1>/dev/null &
echo $! >> pids

../../kafka_2.12-2.4.1/bin/kafka-server-start.sh configs/config1.properties 1>/dev/null &
echo $! >> pids

../../kafka_2.12-2.4.1/bin/kafka-server-start.sh configs/config2.properties 1>/dev/null &
echo $! >> pids

../../kafka_2.12-2.4.1/bin/kafka-server-start.sh configs/config3.properties 1>/dev/null &
echo $! >> pids

for var in "$@"
do

../../kafka_2.12-2.4.1/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 --topic $var

done
