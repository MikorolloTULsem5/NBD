package nbd.gV;

public class SchemaConst {

    //NAMESPACE
    public final static String RESERVE_A_COURT_NAMESPACE = "reserve_a_court";

    //TABLES
    public final static String CLIENTS_TABLE = "clients";
    public final static String COURTS_TABLE = "courts";
    public final static String RESERVATIONS_BY_CLIENT_TABLE = "reservations_by_client";
    public final static String RESERVATIONS_BY_COURT_TABLE = "reservations_by_court";

    //CLIENT FIELDS
    public final static String PERSONAL_ID = "personal_id";
    public final static String CLIENT_TYPE_NAME = "client_type_name";
    public final static String CLIENT_ID = "client_id";
    public final static String FIRST_NAME = "first_name";
    public final static String LAST_NAME = "last_name";

    //COURT FIELDS
    public final static String COURT_NUMBER = "court_number";
    public final static String COURT_ID = "court_id";
    public final static String AREA = "area";
    public final static String BASE_COST = "base_cost";
    public final static String RENTED = "rented";

    //COMMON FIELDS
    public final static String ARCHIVE = "archive";

    //RESERVATIONS FIELDS

    public final static String RESERVATION_ID = "reservation_id";
    public final static String BEGIN_TIME = "begin_time";
    public final static String END_TIME = "end_time";
    public final static String RESERVATION_COST = "reservation_cost";
    public final static String NOT_ENDED = "not_ended";
}
