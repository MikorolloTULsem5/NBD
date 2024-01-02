package nbd.gV;

import nbd.gV.repositories.AbstractCassandraRepository;

public class App {
    public static void main(String[] args) {
        try (AbstractCassandraRepository acr = new AbstractCassandraRepository()) {
            acr.initSession();
            acr.addKeyspace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
