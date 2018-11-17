package by.peekhovsky.messenger.coding;

import org.apache.commons.lang.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;


@SuppressWarnings("WeakerAccess")
public class ByteStuffing {
    //first and last bytes are
    public final static byte F_END_SYMBOL = 0x7E; //126 ~
    public final static byte F_ESC_SYMBOL = 0x7D; //125 }
    public final static byte T_END_SYMBOL = 0x5E; //94
    public final static byte T_ESC_SYMBOL = 0x5D; //93

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
                    //i++;
                }
            }
        }

        Byte[] returnBytes = new Byte[list.size()];
        list.toArray(returnBytes);
        return ArrayUtils.toPrimitive(returnBytes);
    }
}
