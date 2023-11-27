package nbd.gV.courts;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Court.class)
public abstract class Court_ {

	public static volatile SingularAttribute<Court, Double> area;
	public static volatile SingularAttribute<Court, Integer> baseCost;
	public static volatile SingularAttribute<Court, Integer> courtNumber;
	public static volatile SingularAttribute<Court, Boolean> rented;
	public static volatile SingularAttribute<Court, Boolean> archive;
	public static volatile SingularAttribute<Court, UUID> courtId;

	public static final String AREA = "area";
	public static final String BASE_COST = "baseCost";
	public static final String COURT_NUMBER = "courtNumber";
	public static final String RENTED = "rented";
	public static final String ARCHIVE = "archive";
	public static final String COURT_ID = "courtId";

}

