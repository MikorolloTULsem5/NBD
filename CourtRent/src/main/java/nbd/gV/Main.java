package nbd.gV;

public class Main {
    public static void main(String[] args) {
        try {
            Court type1 = new BasketballCourt(10, 100, 1);
            Court type2 = new FootballCourt(10, 100, 2);
            Court type3 = new TennisCourt(10, 100, 3);
            Court type4 = new VolleyballCourt(10, 100, 4);

            System.out.print(type1.getCourtInfo());
            System.out.print(type2.getCourtInfo());
            System.out.print(type3.getCourtInfo());
            System.out.print(type4.getCourtInfo());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}