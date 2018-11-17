package by.peekhovsky;

import by.peekhovsky.HammingCode;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class HammingCodeTest {

    @Test
    public void testHammingCode() {
        String string = "01110101" + "00100101";
        String hammingString = HammingCode.getHammingCode(string);
        String returnString =  HammingCode.inject(hammingString);
        assertEquals(returnString, string);

        string = "11111111" + "11111111";
        hammingString = HammingCode.getHammingCode(string);
        returnString =  HammingCode.inject(hammingString);
        assertEquals(returnString, string);

        string = "00000000" + "00000000";
        hammingString = HammingCode.getHammingCode(string);
        returnString =  HammingCode.inject(hammingString);
        assertEquals(returnString, string);

        string = "00000000" + "11111111";
        hammingString = HammingCode.getHammingCode(string);
        returnString =  HammingCode.inject(hammingString);
        assertEquals(returnString, string);

    }


    @Test
    public void testHammingCodeWithMistake() {
        String string = "01000100" + "00111101";
        String hammingString = HammingCode.getHammingCode(string);
        String returnString =  HammingCode.inject(hammingString.replaceFirst("1", "0"));
        assertEquals(returnString, string);

        string = "01000110" + "11111101";
        hammingString = HammingCode.getHammingCode(string);
        System.out.println(hammingString);
        returnString =  HammingCode.inject("100110010110111111101");
        assertEquals(returnString, string);
    }
}
