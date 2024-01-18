import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import nbd.gV.clients.Client;
import nbd.gV.clients.clienttype.Normal;
import nbd.gV.courts.Court;
import nbd.gV.reservations.Reservation;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Future;

public class App {
    public static void main(String[] args) {
        try (Jsonb jsonb = JsonbBuilder.create()) {


            String json = jsonb.toJson(new Reservation(new Client("Henry", "Bard", "1234", new Normal())
                    , new Court(100, 200, 1), LocalDateTime.now()));
            json = "|MMJ Courts| ~ " + json;
            System.out.println(json.substring(json.indexOf("~") + 1).trim());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
