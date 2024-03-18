docker-compose up​ -d

docker exec -it cassandra1 nodetool status


docker run --rm -it nuvo/docker-cqlsh cqlsh cassandra 9042 --cqlversion='3.4.5'