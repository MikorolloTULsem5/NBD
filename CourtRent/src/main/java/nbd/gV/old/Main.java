package nbd.gV.old;


import nbd.gV.courts.Court;
import nbd.gV.courts.CourtManager;


public class Main {
    public static void main(String[] args) {
        CourtManager courtManager = new CourtManager("default");
        courtManager.registerCourt(41, 11, 3);
        courtManager.registerCourt(111, 111, 4);
        courtManager.registerCourt(2222, 2222, 5);
        courtManager.registerCourt(33333, 33333, 6);

        courtManager.getAllCourts().forEach((c) -> System.out.println(c.getCourtInfo()));
    }
}