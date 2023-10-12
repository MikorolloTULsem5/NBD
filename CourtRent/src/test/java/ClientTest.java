import nbd.gV.clients.Client;
import nbd.gV.clients.Athlete;
import nbd.gV.clients.ClientType;
import nbd.gV.clients.Coach;
import nbd.gV.clients.Normal;

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
