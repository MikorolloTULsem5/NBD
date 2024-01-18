package nbd.gv.consumer;

import nbd.gv.consumer.consumer.ReservationConsumer;

public class AppConsumer {
    public static void main(String[] args) {
        new ReservationConsumer().runConsumerGroup("kafka_messages");
    }
}
