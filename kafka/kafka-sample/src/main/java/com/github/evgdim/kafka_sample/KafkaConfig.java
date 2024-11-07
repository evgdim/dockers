package com.github.evgdim.kafka_sample;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
@EnableKafka
@EnableKafkaStreams
@EnableScheduling
public class KafkaConfig {
    Logger logger = LoggerFactory.getLogger(KafkaConfig.class);
    public static final Serde<String> STRING_SERDE = Serdes.String();
    public static final String INPUT_TOPIC = "input-topic";
    public static final String OUTPUT_TOPIC = "output-topic";
    public static final String WORD_COUNTS_TABLE = "count-table";

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = getKafkaConfigProps(bootstrapAddress);
        return new KafkaStreamsConfiguration(props);
    }

    public static Map<String, Object> getKafkaConfigProps(String bootstrapAddress) {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, "streams-app");
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        return props;
    }


    @Bean
    KStream<String, String> upperCaseStream(StreamsBuilder streamsBuilder) {
        return buildUppercaseStream(streamsBuilder);
    }

    @Bean
    KTable<String, Long> ktable(StreamsBuilder streamsBuilder) {
        return buildKTable(streamsBuilder);
    }

    public static KTable<String, Long> buildKTable(StreamsBuilder streamsBuilder) {
        KStream<String, String> stream = streamsBuilder.stream(INPUT_TOPIC, Consumed.with(STRING_SERDE, STRING_SERDE));
        KTable<String, Long> wordCounts = stream
                    .mapValues((ValueMapper<String, String>) String::toLowerCase)
                    .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                    .groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
                    .count(Materialized.as(WORD_COUNTS_TABLE));
        return wordCounts;
    }

    public static KStream<String, String> buildUppercaseStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> stream = streamsBuilder
                .stream(INPUT_TOPIC, Consumed.with(STRING_SERDE, STRING_SERDE))
                .mapValues(string -> string.toUpperCase());
        stream.to(OUTPUT_TOPIC);
        return stream;
    }


    @KafkaListener(topics = OUTPUT_TOPIC, groupId = "grp-1")
    void listenOutputTopic(ConsumerRecord<String, String> message) {
        logger.info("Received message on {} : {}", OUTPUT_TOPIC, message);
    }


}
