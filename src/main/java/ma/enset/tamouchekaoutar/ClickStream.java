package ma.enset.tamouchekaoutar;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
public class ClickStream {

    @Bean
    public KStream<String, String> processClicks(StreamsBuilder builder) {
        // Setting up Kafka Streams Consumed and Produced Serdes
        Consumed<String, String> consumed = Consumed.with(Serdes.String(), Serdes.String());
        Produced<String, Long> produced = Produced.with(Serdes.String(), Serdes.Long());

        // Stream the "clicks" topic
        KStream<String, String> clicksStream = builder.stream("clicks", consumed);

        // Count the clicks per user (grouped by user ID) and send the result to the "click-counts" topic
        clicksStream
                .groupByKey()
                .count(Materialized.as("clicks-count-store"))  // Store the count in a local state store
                .toStream()
                .to("click-counts", produced);  // Write results to the "click-counts" topic

        return clicksStream;
    }
}
