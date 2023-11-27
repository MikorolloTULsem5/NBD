package nbd.gV.reservations;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import nbd.gV.clients.Client;
import nbd.gV.courts.Court;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Reservation.class)
public abstract class Reservation_ {

	public static volatile SingularAttribute<Reservation, Client> client;
	public static volatile SingularAttribute<Reservation, UUID> id;
	public static volatile SingularAttribute<Reservation, LocalDateTime> beginTime;
	public static volatile SingularAttribute<Reservation, LocalDateTime> endTime;
	public static volatile SingularAttribute<Reservation, Court> court;
	public static volatile SingularAttribute<Reservation, Double> reservationCost;

	public static final String CLIENT = "client";
	public static final String ID = "id";
	public static final String BEGIN_TIME = "beginTime";
	public static final String END_TIME = "endTime";
	public static final String COURT = "court";
	public static final String RESERVATION_COST = "reservationCost";

}

