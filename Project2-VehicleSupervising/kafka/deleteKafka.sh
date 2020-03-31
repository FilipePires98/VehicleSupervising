while read p; do
    echo "Process: "$p
  kill -9 $p
done <pids

rm -rf kafkaSrc/kafka-logs-1
rm -rf kafkaSrc/kafka-logs-2
rm -rf kafkaSrc/kafka-logs-3
rm -rf kafkaSrc/zookeeper