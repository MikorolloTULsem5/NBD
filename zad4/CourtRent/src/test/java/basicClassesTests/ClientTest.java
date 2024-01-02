package basicClassesTests;

import nbd.gV.clients.Client;
import nbd.gV.clients.clienttype.Athlete;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.clients.clienttype.Coach;
import nbd.gV.clients.clienttype.Normal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nbd.gV.exceptions.MainException;
import org.junit.jupiter.api.Test;

public class ClientTest {
    String testFirstName = "John";
    String testLastName = "Smith";
    String testPersonalID = "12345678";
    ClientType testTypeAthlete = new Athlete();
    ClientType testTypeCoach = new Coach();
    ClientType testTypeNormal = new Normal();
    @Test
    void testCreatingClient() {
        Client client = new Client(testFirstName, testLastName, testPersonalID, testTypeNormal.getClientTypeName());
        assertNotNull(client);

        assertEquals(testFirstName, client.getFirstName());
        assertEquals(testLastName, client.getLastName());
        assertEquals(testPersonalID, client.getPersonalId());
        assertEquals(testTypeNormal.getClientTypeName(), client.getClientType().getClientTypeName());
        assertFalse(client.isArchive());

        assertThrows(MainException.class, ()
                -> new Client("", testLastName, testPersonalID, testTypeNormal.getClientTypeName()));
        assertThrows(MainException.class, ()
                -> new Client(testFirstName, "", testPersonalID, testTypeNormal.getClientTypeName()));
        assertThrows(MainException.class, ()
                -> new Client(testFirstName, testLastName, "", testTypeNormal.getClientTypeName()));
    }

    @Test
    void testSetters() {
        Client client = new Client(testFirstName, testLastName, testPersonalID, testTypeNormal.getClientTypeName());
        assertNotNull(client);

        assertEquals(testFirstName, client.getFirstName());
        client.setFirstName("Adam");
        assertEquals("Adam", client.getFirstName());

        assertEquals(testLastName, client.getLastName());
        client.setLastName("Long");
        assertEquals("Long", client.getLastName());

        assertFalse(client.isArchive());
        client.setArchive(true);
        assertTrue(client.isArchive());
        client.setArchive(false);
        assertFalse(client.isArchive());
    }

    @Test
    void testGettingClientMaxHoursAndApplyingDiscount() {
        Client client = new Client(testFirstName, testLastName, testPersonalID, testTypeNormal.getClientTypeName());
        assertNotNull(client);
        Client client1 = new Client(testFirstName, testLastName, testPersonalID, testTypeAthlete.getClientTypeName());
        assertNotNull(client1);
        Client client2 = new Client(testFirstName, testLastName, testPersonalID, testTypeCoach.getClientTypeName());
        assertNotNull(client2);

        assertEquals(0, client.applyDiscount(100));
        assertEquals(10, client1.applyDiscount(100));
        assertEquals(15, client2.applyDiscount(100));

        assertEquals(3, client.getClientMaxHours());
        assertEquals(6, client1.getClientMaxHours());
        assertEquals(12, client2.getClientMaxHours());
    }
}
