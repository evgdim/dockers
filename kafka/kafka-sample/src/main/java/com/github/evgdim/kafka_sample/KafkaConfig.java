package com.github.evgdim.kafka_sample;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaConfig {
    Logger logger = LoggerFactory.getLogger(KafkaConfig.class);
    private static final Serde<String> STRING_SERDE = Serdes.String();
    public static final String INPUT_TOPIC = "input-topic";
    public static final String OUTPUT_TOPIC = "output-topic";

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, "streams-app");
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        return new KafkaStreamsConfiguration(props);
    }


    @Bean
    KStream<String, String> buildPipeline(StreamsBuilder streamsBuilder) {
        KStream<String, String> stream = streamsBuilder
                .stream(INPUT_TOPIC, Consumed.with(STRING_SERDE, STRING_SERDE))
                .mapValues(string -> string.toUpperCase());
        stream.to(OUTPUT_TOPIC);
        return stream;

//        KTable<String, Long> wordCounts = messageStream
//                .mapValues((ValueMapper<String, String>) String::toLowerCase)
//                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
//                .groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
//                .count();

    }


    @KafkaListener(topics = OUTPUT_TOPIC, groupId = "grp-1")
    void listenOutputTopic(ConsumerRecord<String, String> message) {
        logger.info("Received message on {} : {}", OUTPUT_TOPIC, message);
    }
}
