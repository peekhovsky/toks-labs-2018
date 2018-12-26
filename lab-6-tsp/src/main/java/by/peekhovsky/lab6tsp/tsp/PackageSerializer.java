package by.peekhovsky.lab6tsp.tsp;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Parser for Token class
 * Token structure:
 * #address-tag# #address-return-tag# message
 *
 * @author Rostislav Pekhovsky
 * @version 0.1
 * @see Token
 */
@SuppressWarnings({"WeakerAccess", "Duplicates"})
@Log4j2
public final class PackageSerializer {

    private static final Pattern PACKAGE_TAG_REGEX = Pattern.compile("#package#[A-Za-z\\w]*");
    private static final Pattern SN_REGEX = Pattern.compile("#sn#[\\w]+");
    private static final Pattern AN_REGEX = Pattern.compile("#an#[-\\w]+");
    private static final Pattern CHECKSUM_REGEX = Pattern.compile("#chs#[\\w]+");
    private static final Pattern MESSAGE_REGEX = Pattern.compile("#mes#[.\\p{all}]*");

    private static final String USUAL_PACKAGE_TAG = "usual";
    private static final String RETURN_PACKAGE_TAG = "return";

    /**
     * @param p data to serialize
     * @return serialized list of tokens
     */
    public static String serialize(@NonNull final Package p) {
        String res;
        if (p.isReturn()) {
            res = "#package#" + RETURN_PACKAGE_TAG + "\n"
                    + "#an#"+ p.getAn() + "\n";
        } else {
            res = "#package#" + USUAL_PACKAGE_TAG + "\n"
                    + "#sn#" + p.getSn() + "\n"
                    + "#an#" + p.getAn() + "\n"
                    + "#chs#" + p.getCheckSum() + "\n"
                    + "#mes#" + p.getMessage() + "\n";
        }
        return res;
    }


    /**
     * @param line string with data to make package object
     * @return optional of package
     */
    public static Optional<Package> deserialize(@NonNull final String line)
            throws IllegalStateException {

        log.debug("Line to deserialize: " + line);
        Optional<Package> packageOptional;
        try (Scanner scanner = new Scanner(line)) {

            String addressTagTemp = scanner.next(PACKAGE_TAG_REGEX);
            if (addressTagTemp.length() > 2) {
                addressTagTemp = addressTagTemp
                        .replace("#package#", "");
                //log.debug("Address tag: " + addressTagTemp);
                if (addressTagTemp.equals(USUAL_PACKAGE_TAG)) {
                    packageOptional = deserializeUsualPackage(line, scanner);
                } else if (addressTagTemp.equals(RETURN_PACKAGE_TAG)) {
                    packageOptional = deserializeReturnPackage(line, scanner);
                } else {
                    log.warn("Cannot deserialize message.");
                    packageOptional = Optional.empty();
                }
            } else {
                log.warn("Cannot deserialize message.");
                packageOptional = Optional.empty();
            }
        }

        return packageOptional;
    }

    private static Optional<Package> deserializeUsualPackage(@NonNull final String line, Scanner scanner) {

        Optional<Package> packageOptional = Optional.empty();

        Package pack = new Package();

        try {

                String sn = scanner.next(SN_REGEX);
                sn = sn.replace("#sn#", "");
                pack.setSn(Integer.parseInt(sn));

                String an = scanner.next(AN_REGEX);
                an = an.replace("#an#", "");
                pack.setAn(Integer.parseInt(an));

                String checkSum = scanner.next(CHECKSUM_REGEX);
                checkSum = checkSum.replace("#chs#", "");
                pack.setCheckSum(Integer.parseInt(checkSum));

            scanner.nextLine();
                String message = scanner.next(MESSAGE_REGEX);
                message = message.replace("#mes#", "");
                pack.setMessage(message);

                pack.setReturn(false);

                packageOptional = Optional.of(pack);

                //log.debug("Package: " + pack);

        } catch (IllegalStateException e) {
            log.warn("Cannot deserialize message.");
            packageOptional = Optional.empty();
        } catch (InputMismatchException e) {
            log.error("Input mismatch: " + e.getMessage());
            e.printStackTrace();
        }
        return packageOptional;
    }

    private static Optional<Package> deserializeReturnPackage(@NonNull final String line, Scanner scanner) {

        Optional<Package> packageOptional;

        Package pack = new Package();

        try {
                String an = scanner.next(AN_REGEX);
                an = an.replace("#an#", "");
                pack.setAn(Integer.parseInt(an));

                pack.setReturn(true);

                packageOptional = Optional.of(pack);

        } catch (IllegalStateException e) {
            log.warn("Cannot deserialize message.");
            packageOptional = Optional.empty();
        }
        log.debug("Package: " + pack);
        return packageOptional;
    }

    /***
     * Private no args constructor.
     */
    private PackageSerializer() {
    }

}
