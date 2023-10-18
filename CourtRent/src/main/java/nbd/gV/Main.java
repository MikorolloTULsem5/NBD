package nbd.gV;


import nbd.gV.courts.Court;
import nbd.gV.courts.CourtManager;
import nbd.gV.exceptions.CourtException;
import nbd.gV.repositories.CourtRepository;

import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        CourtManager courtManager = new CourtManager("default");
        Court testCourt3 = new Court(41,11,3);
        Court cc = courtManager.registerCourt(41, 11, 3);
        courtManager.unregisterCourt(cc);
    }
}