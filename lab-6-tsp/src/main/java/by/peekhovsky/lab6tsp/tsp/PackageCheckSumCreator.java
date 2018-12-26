package by.peekhovsky.lab6tsp.tsp;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class PackageCheckSumCreator {
    public static int addCheckSumToPackage(@NonNull Package pack) {
        String s = pack.getMessage() + pack.getAn() + pack.getSn();
        pack.setCheckSum(s.length());
        return s.length();
    }

    public static boolean validateCheckSumToPackage(@NonNull Package pack) {
        String s = pack.getMessage() + pack.getAn() + pack.getSn();
        return s.length() == pack.getCheckSum();
    }
}
