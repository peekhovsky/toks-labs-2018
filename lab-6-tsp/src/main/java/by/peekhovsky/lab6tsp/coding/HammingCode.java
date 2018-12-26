package by.peekhovsky.lab6tsp.coding;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/***
 * @author Rostislav Pekhovsky
 * @version 0.1
 */
@SuppressWarnings("WeakerAccess")
@Log4j2
public final class HammingCode {

    private HammingCode() {
    }

    public static String getHammingCodeFromBytes(byte[] bytes) {

        StringBuilder returnString = new StringBuilder();

        for (int i = 0; i < bytes.length; i += 2) {
            byte[] pair = new byte[2];
            pair[0] = bytes[i];
            pair[1] = 0;
            if (i + 1 < bytes.length) {
                pair[1] = bytes[i + 1];
            }
            returnString.append(getHammingCode(getBitsFromBytes(pair)));
        }
        log.trace("Return string: " + returnString);
        return returnString.toString();
    }

    public static byte[] getBytesFromHammingCode(String bits) {

        ArrayList<Byte> bytes = new ArrayList<>();

        for (int i = 0; i < bits.length(); i += 21) {

            log.trace("Bits: " + bits.substring(i, i + 21));

            byte[] pair = getBytesFromBits(
                    inject(bits.substring(i, i + 21)));

            bytes.add(i / 21 * 2, pair[0]);
            bytes.add(i / 21 * 2 + 1, pair[1]);

        }

        bytes.trimToSize();
        if (bytes.get(bytes.size() - 1) == 0) {
            bytes.remove(bytes.size() - 1);
        }
        bytes.trimToSize();
        return ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
    }

    private static String getBitsFromBytes(byte[] bytes) {
        StringBuilder bits = new StringBuilder(bytes.length * 8);
        int j = 0;
        for (byte byte_ : bytes) {

            String s = Integer.toBinaryString(byte_);
            bits.replace(j, j + 8, "00000000");
            bits.replace(j + (8 - s.length()), j + 8, s);
            j += 8;
        }
        return bits.toString();
    }

    private static byte[] getBytesFromBits(String bits) {
        if (bits.length() % 8 != 0) {
            System.out.println("Bits: " + bits);
            throw new RuntimeException("Cannot get bites deserialize bits! Bits are not divisible on 8.");
        }
        byte[] bytes = new byte[bits.length() / 8];
        int bytesPointer = 0;
        for (int i = 0; i < bits.length(); i += 8) {

            String bit = bits.substring(i, i + 8);
            byte b = (byte) Integer.parseInt(bit, 2);

            bytes[bytesPointer] = b;
            bytesPointer++;
        }

        return bytes;
    }


    public static String getHammingCode(String s) {

        StringBuilder binaryString = new StringBuilder("N" + s);

        int powered = 0;

        for (int i = 0; powered < s.length(); i++) {
            powered = (int) Math.pow(2, i);
            binaryString.insert(powered, "a");
        }

        powered = 0;

        for (int i = 0; powered < s.length(); i++) {
            powered = (int) Math.pow(2, i);

            int evenness = 1;

            for (int j = powered; j < binaryString.length(); j += (powered * 2)) {

                char chars[] = new char[powered];
                if ((j + powered) < binaryString.length()) {
                    binaryString.getChars(j, j + powered, chars, 0);
                } else {
                    binaryString.getChars(j, binaryString.length(), chars, 0);
                }

                for (char c : chars) {
                    if (c == '1') {
                        evenness *= -1;
                    }
                }

                if (evenness == 1) {
                    binaryString.setCharAt(powered, '0');
                } else {
                    binaryString.setCharAt(powered, '1');
                }
            }
        }

        return binaryString.toString().replace("N", "");
    }

    public static String inject(String s) {

        StringBuilder binaryString = new StringBuilder("N" + s);
        int powered = 1;
        int errorPosition = 0;

        for (int i = 0; powered < binaryString.length(); i++) {

            powered = (int) Math.pow(2, i);

            if (powered > binaryString.length()) {
                break;
            }

            int evenness = 1;
            int controlBit = binaryString.charAt(powered) == '1' ? 1 : 0;

            for (int j = powered; j < binaryString.length(); j += (powered * 2)) {

                char[] chars = new char[powered];
                if ((j + powered) < binaryString.length()) {
                    binaryString.getChars(j, j + powered, chars, 0);
                } else {
                    binaryString.getChars(j, binaryString.length(), chars, 0);
                }

                if (j == powered) {
                    chars[0] = '0';
                }

                for (char c : chars) {
                    if (c == '1') {
                        evenness *= -1;
                    }
                }
            }

            if ((evenness == 1 && controlBit == 1) || (evenness == -1 && controlBit == 0)) {
                errorPosition += powered;
                log.trace("Error position: " + errorPosition);
            }
        }


        if (errorPosition > 0) {
            log.trace("Mistake has been detected. Position: " + errorPosition);
            binaryString.setCharAt(errorPosition, binaryString.charAt(errorPosition) == '1' ? '0' : '1');
        }


        powered = 0;

        for (int i = 0; powered < binaryString.length(); i++) {
            binaryString.setCharAt(powered, 'a');
            powered = (int) Math.pow(2, i);
        }

        return binaryString.toString().replace("a", "");
    }
}
