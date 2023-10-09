import nbd.gV.Client;
import nbd.gV.clientstype.Athlete;
import nbd.gV.clientstype.ClientType;
import nbd.gV.clientstype.Coach;
import nbd.gV.clientstype.Normal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ClientTest {
    String testFirstName = "John";
    String testLastName = "Smith";
    String testPersonalID = "12345678";
    ClientType testTypeAthlete = new Athlete();
    ClientType testTypeCoach = new Coach();
    ClientType testTypeNormal = new Normal();
    @Test
    void testConstructor() {
        Client client = new Client(testFirstName, testLastName, testPersonalID, testTypeNormal);
        assertNotNull(client);
    }
}
