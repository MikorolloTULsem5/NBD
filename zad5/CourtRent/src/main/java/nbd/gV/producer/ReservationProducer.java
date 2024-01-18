package nbd.gV.producer;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import nbd.gV.exceptions.MainException;
import nbd.gV.mappers.ReservationMapper;
import nbd.gV.reservations.Reservation;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.UUIDSerializer;

import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ReservationProducer {
    public static final String TOPIC = "court_reservations";

    private static Producer<UUID, String> producer;

    public void createTopic() throws InterruptedException {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");

        int numOfPartitions = 3;
        short repFactor = 2;

        try (Admin admin = Admin.create(properties)) {
            NewTopic newTopic = new NewTopic(TOPIC, numOfPartitions, repFactor);
            CreateTopicsOptions options = new CreateTopicsOptions()
                    .timeoutMs(10000)
                    .validateOnly(false)
                    .retryOnQuotaViolation(true);
            CreateTopicsResult result = admin.createTopics(List.of(newTopic), options);
            KafkaFuture<Void> futureResult = result.values().get(TOPIC);
            futureResult.get();
        } catch (ExecutionException ee) {
            System.out.println(ee.getMessage());
        }
    }

    public static void initProducer() throws ExecutionException, InterruptedException {
        Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class.getName());
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, "local");
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");
        producerConfig.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "536bba71-4884-4bc8-b1ad-1eb3f3e54bb5");
        producer = new KafkaProducer<>(producerConfig);
    }

    public void sendMessage(ReservationMapper reservation) throws ExecutionException, InterruptedException {
        initProducer();
        producer.initTransactions();
        try(Jsonb jsonb = JsonbBuilder.create()) {
            producer.beginTransaction();

            String json = jsonb.toJson(reservation);
            json = "|MMJ Courts| ~ " + json;

            ProducerRecord<UUID, String> record = new ProducerRecord<>(TOPIC, UUID.fromString(reservation.getId()), json);
            producer.send(record);

            producer.commitTransaction();
        } catch (ProducerFencedException exception) {
            producer.close();
        } catch (KafkaException exception) {
            producer.abortTransaction();
        } catch (Exception e) {
            throw new MainException("Serialization problem");
        }
    }
}
