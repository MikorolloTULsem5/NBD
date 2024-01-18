package nbd.gv.consumer;

import nbd.gv.consumer.model.Reservation;
import nbd.gv.consumer.repository.AbstractMongoRepository;
import nbd.gv.consumer.repository.ReservationMongoRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppConsumer {
    public static void main(String[] args) {
        AbstractMongoRepository<Reservation> repository = new ReservationMongoRepository("test_kafka");
        repository.create(new Reservation(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now(), 200));
    }
}
