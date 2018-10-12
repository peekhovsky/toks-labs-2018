package by.peekhovsky;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class HammingCode {

    public static String getHammingCodeFromBytes(byte[] bytes) {


    }

    public static byte[] getBytesFromHammingCode(String bits) {

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
            throw new RuntimeException("Cannot get bites from bits! Bits are not divisible on 8.");
        }

        byte[] bytes = new byte[bits.length() / 8];
        int bytesPointer = 0;

        for (int i = 0; i < bits.length(); i+=8) {

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

                char chars[] = new char[powered];
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

            if ((evenness == 1 && controlBit == 1) || (evenness == -1 && controlBit == 0) ) {
                errorPosition += powered;
                System.out.println("Error position: " + errorPosition);
            }
        }


        if (errorPosition > 0) {
            System.err.println("Mistake has been detected. Position: " + errorPosition);
            binaryString.setCharAt(errorPosition, binaryString.charAt(errorPosition) == '1' ? '0' : '1');
        }


        powered = 0;

        for (int i = 0; powered < binaryString.length(); i++) {
            binaryString.setCharAt(powered, 'a');
            powered = (int) Math.pow(2, i);
        }

        return binaryString.toString().replace("a", "");
    }





    public static void main(String[] args) {

        byte bytes[] = {34, 56};
        System.out.println(Arrays.toString(bytes));
        String bits = getHammingCode(getBitsFromBytes(bytes));
        byte[] returnBytes =getBytesFromBits(inject(bits));
        System.out.println(Arrays.toString(returnBytes));
    }
}
