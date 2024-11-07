package com.github.evgdim.kafka_sample;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;


public class StreamTests {

    @Test
    void testUpperCase() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KafkaConfig.buildUppercaseStream(streamsBuilder);
        Topology topology = streamsBuilder.build();
        Properties props = new Properties();
        KafkaConfig.getKafkaConfigProps("").forEach((key, value) -> {
            props.setProperty(key, value.toString());
        });
        try(TopologyTestDriver ttd = new TopologyTestDriver(topology, props)) {
            TestInputTopic<String, String> inputTopic = ttd.createInputTopic(KafkaConfig.INPUT_TOPIC, new StringSerializer(), new StringSerializer());
            TestOutputTopic<String, String> outputTopic = ttd.createOutputTopic(KafkaConfig.OUTPUT_TOPIC, new StringDeserializer(), new StringDeserializer());

            inputTopic.pipeInput("key", "hello world");
            inputTopic.pipeInput("key2", "hello");

            assertThat(outputTopic.readKeyValuesToList())
                    .containsExactly(
                            KeyValue.pair("key", "HELLO WORLD"),
                            KeyValue.pair("key2", "HELLO")
                    );
        }
    }
}
