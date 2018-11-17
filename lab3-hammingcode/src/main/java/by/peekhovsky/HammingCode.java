package by.peekhovsky;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class HammingCode {

    private final static List<Integer> ILLEGAL_MISTAKE_POSITIONS = Arrays.asList(1, 2, 4, 8, 16);


    public static String getHammingCodeFromBytes(byte[] bytes) {

        StringBuilder returnString = new StringBuilder();

        for (int i = 0; i < bytes.length; i += 2) {
            byte[] pair = new byte[2];
           pair[0] = bytes[i];
            if (i + 1 < bytes.length) {
                pair[1] = bytes[i + 1];
            }

            returnString.append(getHammingCode(getBitsFromBytes(pair)));
        }
        //System.out.println("Return string: " + returnString);
        return returnString.toString();
    }

    public static String getHammingCodeFromBytes(byte[] bytes, int mistakePosition) {

        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < bytes.length; i += 2) {
            byte[] pair = new byte[2];
            pair[0] = bytes[i];
            if (i + 1 < bytes.length) {
                pair[1] = bytes[i + 1];
            }

            if (!ILLEGAL_MISTAKE_POSITIONS.contains(mistakePosition) && mistakePosition != -1 && mistakePosition < 21) {
               StringBuilder stringBuilder = new StringBuilder(getHammingCode(getBitsFromBytes(pair)));
               if (stringBuilder.charAt(mistakePosition) == '0') {
                   stringBuilder.setCharAt(mistakePosition, '1');
               } else {
                   stringBuilder.setCharAt(mistakePosition, '0');
               }
               returnString.append(stringBuilder.toString()).append("000");
            } else {
                returnString.append(getHammingCode(getBitsFromBytes(pair))).append("000");
            }
        }
        //System.out.println("Return string: " + returnString);
        return returnString.toString();
    }

    public static byte[] getBytesFromHammingCode(String bits) {

        ArrayList<Byte> bytes  = new ArrayList<>();

        for (int i = 0; i < bits.length() - 10; i += 24) {
            //System.out.println("Bits: " + bits.substring(i, i + 21));
            byte[] pair = getBytesFromBits(
                    inject(bits.substring(i, i + 21)));

            bytes.add(i / 24 * 2, pair[0]);
            bytes.add(i / 24 * 2 + 1, pair[1]);
        }

        bytes.trimToSize();
        if (bytes.get(bytes.size() - 1) == 0) {
            bytes.remove(bytes.size() - 1);
        }
        bytes.trimToSize();
        return ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
    }

    public static String getBitsFromBytes(byte[] bytes) {
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

    public static byte[] getBytesFromBits(String bits) {
        if (bits.length() % 8 != 0) {
            System.out.println("Bits: " + bits);
            throw new RuntimeException("Cannot get bites from bits! Bits are not divisible on 8.");
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
                        evenness ^= (int) c;
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
                //System.out.println("Error position: " + errorPosition);
            }
        }

        if (errorPosition > 0) {
            binaryString.setCharAt(errorPosition, binaryString.charAt(errorPosition) == '1' ? '0' : '1');
            System.err.println("Mistake has been detected. Position: " + (errorPosition - 1));
        }


        powered = 0;

        for (int i = 0; powered < binaryString.length(); i++) {
            binaryString.setCharAt(powered, 'a');
            powered = (int) Math.pow(2, i);
        }


        return binaryString.toString().replace("a", "");
    }

    public static byte[] getBytesFromBitString(String bits) {
        System.out.println("bits: " + bits);
        byte postSize = 0;
        StringBuilder bitsBuilder = new StringBuilder(bits);

        while (bitsBuilder.length() % 8 != 0) {
            postSize += 1;
            System.out.println("Byte: " + postSize);
            bitsBuilder.append('0');
            bitsBuilder.trimToSize();
        }

        bits = bitsBuilder.toString();

        byte[] bytes = new byte[bits.length() / 8 + 1];
        bytes[0] = postSize;

        int bytesPointer = 1;
        for (int i = 8; i < bits.length(); i += 8) {

            String bit = bits.substring(i, i + 8);
            byte b = (byte) Integer.parseInt(bit, 2);

            bytes[bytesPointer] = b;
            bytesPointer++;
        }
        System.out.println("Send bytes: " + Arrays.toString(bytes));
        return bytes;
    }

    synchronized public static String getBitStringFromBytes(byte[] bytes) {
        int postSize = bytes[0];
        System.out.println("Post size: " + postSize);
        StringBuilder bits = new StringBuilder(bytes.length * 8);

        int j = 0;

        for (byte byte_: bytes) {
            String s = Integer.toBinaryString(byte_);
            System.out.println(byte_);
            System.out.println(s);

                bits.replace(j, j + 8, "00000000");
                bits.replace(j + (8 - s.length()), j + 8, s);

            j += 8;
        }

        bits.delete(0, 8);
        bits.trimToSize();

        while (postSize > 0) {
            bits.deleteCharAt(bits.length() - 1);
            bits.trimToSize();
            postSize--;
        }

        System.out.println("After conv: " + bits.toString());
        return bits.toString();
    }

}
