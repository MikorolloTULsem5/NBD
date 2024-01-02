package nbd.gV;

import nbd.gV.clients.Client;
import nbd.gV.repositories.AbstractCassandraRepository;
import nbd.gV.repositories.clients.ClientDao;
import nbd.gV.repositories.clients.ClientMapper;
import nbd.gV.repositories.clients.ClientMapperBuilder;

public class App {
    public static void main(String[] args) {
        try (AbstractCassandraRepository acr = new AbstractCassandraRepository()) {
            acr.initSession();
            acr.addKeyspace();
            acr.createClientsTable();

            ClientMapper clientMapper = new ClientMapperBuilder(AbstractCassandraRepository.getSession()).build();
            ClientDao clientDao = clientMapper.clientDao();

            Client client = new Client("Adam", "Smith", "12345678903", "normal");
            clientDao.create(client);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
