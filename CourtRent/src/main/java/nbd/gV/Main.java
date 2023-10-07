package nbd.gV;

import nbd.gV.clientstype.Athlete;
import nbd.gV.clientstype.ClientType;
import nbd.gV.clientstype.Coach;
import nbd.gV.clientstype.Normal;

public class Main {
    public static void main(String[] args) {
        ClientType clientType = new Athlete();
        ClientType clientType2 = new Coach();
        ClientType clientType3 = new Normal();

        System.out.print(clientType.getTypeInfo());
        System.out.print(clientType2.getTypeInfo());
        System.out.print(clientType3.getTypeInfo());
    }
}