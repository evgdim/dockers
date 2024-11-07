package com.github.evgdim.kafka_sample;

import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.github.evgdim.kafka_sample.KafkaConfig.WORD_COUNTS_TABLE;

@Component
public class KtableStoreListener {
    Logger logger = LoggerFactory.getLogger(KtableStoreListener.class);

    @Autowired
    StreamsBuilderFactoryBean streamsFactoryBean;

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    void pingStore() {
        ReadOnlyKeyValueStore<String, Long> store = streamsFactoryBean.getKafkaStreams().store(
                StoreQueryParameters
                        .fromNameAndType(WORD_COUNTS_TABLE, QueryableStoreTypes.keyValueStore())
        );
        KeyValueIterator<String, Long> all = store.all();
        while (all.hasNext()) {
            logger.info("Store: {}", all.next());
        }
    }
}
