while read p; do
    echo "Process: "$p
  kill -9 $p
done <pids

rm -rf configs/kafka-logs-1
rm -rf configs/kafka-logs-2
rm -rf configs/kafka-logs-3
rm -rf configs/zookeeper