package nbd.gV.repositories;

import jakarta.persistence.criteria.CriteriaQuery;
import nbd.gV.clients.Client;

import java.util.List;
import java.util.UUID;

public class ClientRepository extends Repository<Client> {

    public ClientRepository(String unitName) {
        super(unitName);
    }

    @Override
    public Client findByUUID(UUID identifier) {
        return null;
    }

    @Override
    public List<Client> findAll() {
        return null;
    }

    @Override
    public List<Client> find(CriteriaQuery<Client> query) {
        return null;
    }
}
