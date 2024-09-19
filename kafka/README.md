Creating a Kafka Topic

```
$ docker-compose exec kafka kafka-topics.sh --create --topic baeldung_linux
  --partitions 1 --replication-factor 1 --bootstrap-server kafka:9092
```

List topic
```
$ docker-compose exec kafka kafka-topics.sh --list --bootstrap-server kafka:9092
```



Publishing and Consuming Messages

```
$ docker-compose exec kafka kafka-console-consumer.sh --topic baeldung --from-beginning --bootstrap-server kafka:9092
```

```
$ docker-compose exec kafka kafka-console-producer.sh --topic baeldung --broker-list kafka:9092
```