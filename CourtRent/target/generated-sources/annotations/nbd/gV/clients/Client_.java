package nbd.gV.clients;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.util.UUID;
import javax.annotation.processing.Generated;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Client.class)
public abstract class Client_ {

	public static volatile SingularAttribute<Client, String> firstName;
	public static volatile SingularAttribute<Client, String> lastName;
	public static volatile SingularAttribute<Client, String> personalId;
	public static volatile SingularAttribute<Client, UUID> clientID;
	public static volatile SingularAttribute<Client, ClientType> clientType;
	public static volatile SingularAttribute<Client, Boolean> archive;

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String PERSONAL_ID = "personalId";
	public static final String CLIENT_ID = "clientID";
	public static final String CLIENT_TYPE = "clientType";
	public static final String ARCHIVE = "archive";

}

