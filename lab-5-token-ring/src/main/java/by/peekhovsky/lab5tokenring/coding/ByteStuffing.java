package by.peekhovsky.lab5tokenring.coding;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Rostislav Pekhovsky
 * @version 0.1
 * */
@SuppressWarnings("WeakerAccess")
public final class ByteStuffing {
    /**
     * ASCII 126 "~"
     * */
    public final static byte F_END_SYMBOL = 0x7E;
    /**
     * ASCII 125 "}"
     * */
    public final static byte F_ESC_SYMBOL = 0x7D;
    /**
     * ASCII 94
     */
    public final static byte T_END_SYMBOL = 0x5E;
    /**
     * ASCII 93
     */
    public final static byte T_ESC_SYMBOL = 0x5D;

    /**
     * Private constructor.
     * */
    private ByteStuffing() {
    }

    /***
     * @param bytes bytes to stuff
     * @return stuffed bytes
     */
    public static byte[] doStuffing(byte[] bytes) {

        ArrayList<Byte> list = new ArrayList<>
                (Arrays.asList
                        (ArrayUtils.toObject(bytes)));

        list.add(0, F_END_SYMBOL);

        for (int i = 1; i < list.size(); i++) {
            list.trimToSize();
            if (list.get(i) == F_ESC_SYMBOL) {
                list.add(i+1, T_ESC_SYMBOL);
            }
            else if (list.get(i) == F_END_SYMBOL) {
                list.set(i, F_ESC_SYMBOL);
                list.add(i+1, T_END_SYMBOL);
            }
        }

        Byte[] returnBytes = new Byte[list.size()];
        list.toArray(returnBytes);
        return ArrayUtils.toPrimitive(returnBytes);
    }

    /***
     * @param bytes bytes to inject
     * @return injected bytes
     */
    public static byte[] inject(byte[] bytes) {
        ArrayList<Byte> list = new ArrayList<>
                (Arrays.asList
                        (ArrayUtils.toObject(bytes)));
        list.remove(0);

        for (int i = 0; i < list.size(); i++) {
            list.trimToSize();
            if (list.get(i) == F_ESC_SYMBOL) {
                if (list.get(i + 1) == T_END_SYMBOL) {
                    list.set(i, F_END_SYMBOL);
                    list.remove(i+1);
                }
                else if (list.get(i+1) == T_ESC_SYMBOL) {
                    list.remove(i + 1);
                }
            }
        }
        Byte[] returnBytes = new Byte[list.size()];
        list.toArray(returnBytes);
        return ArrayUtils.toPrimitive(returnBytes);
    }
}
