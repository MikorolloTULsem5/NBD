package nbd.gv.consumer.consumer;

import jakarta.json.bind.JsonbBuilder;

import nbd.gv.consumer.model.Reservation;
import nbd.gv.consumer.repository.ReservationMongoRepository;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservationConsumer {

    public static final String TOPIC = "court_reservations";

    public KafkaConsumer<UUID, String> initConsumer() {
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "reservation_client_group");
        consumerProperties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka:9292,kafka:9392");
        return new KafkaConsumer<>(consumerProperties);
    }

    public List<KafkaConsumer<UUID, String>> createConsumerGroup() {
        List<KafkaConsumer<UUID, String>> consumers = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            KafkaConsumer<UUID, String> newConsumer = initConsumer();
            newConsumer.subscribe(List.of(TOPIC));
            consumers.add(newConsumer);
        }

        return consumers;
    }

    public void receiveClients(KafkaConsumer<UUID, String> consumer, ReservationMongoRepository repository) {
        try {
            consumer.poll(0);
            Set<TopicPartition> consumerAssigment = consumer.assignment();
            consumer.seekToBeginning(consumerAssigment);
            Duration timeout = Duration.of(100, ChronoUnit.MILLIS);

            while (true) {
                ConsumerRecords<UUID, String> records = consumer.poll(timeout);
                for (ConsumerRecord<UUID, String> record : records) {
                    String json = record.value().substring(record.value().indexOf("~") + 1).trim();
                    Reservation reservation = JsonbBuilder.create().fromJson(json, Reservation.class);
                    repository.create(reservation);
                }
            }
        } catch (WakeupException exception) {
            exception.getMessage();
        }
    }

    public void runConsumerGroup(String dbName) {
        List<KafkaConsumer<UUID, String>> consumers = createConsumerGroup();
        ExecutorService executorService = Executors.newFixedThreadPool(consumers.size());

        for (int i = 0; i < consumers.size(); i++) {
            ReservationMongoRepository repository = new ReservationMongoRepository(dbName + "_" + i);
            int finalIteratorValue = i;
            executorService.execute(() -> receiveClients(consumers.get(finalIteratorValue), repository));
        }
    }
}
