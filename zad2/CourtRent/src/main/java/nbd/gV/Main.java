package nbd.gV;

public class Main {

    public static void main(String[] args) {
        try (AbstractMongoRepository repo = new AbstractMongoRepository()) {
            repo.testMethod();
        } catch (Exception e) {
            System.out.println("test");
        }
    }
}
