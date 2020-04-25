hadoop fs -mkdir stddev_i 2> /dev/null
hadoop fs -put data/data.json stddev_i/ 2> /dev/null
hadoop fs -rm -R stddev_o 2> /dev/null
hadoop jar target/zad1B*.jar StandardDeviation stddev_i stddev_o
