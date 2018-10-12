import by.peekhovsky.ByteStuffing;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class StuffingTest {

    @Test
    public void testStuffing() {
        byte[] bytes = "~~~".getBytes();
        byte[] sufferedBytes = ByteStuffing.doStuffing(bytes);
        byte[] injectedBytes = ByteStuffing.inject(sufferedBytes);

        assertTrue(Arrays.equals(bytes, injectedBytes));

        bytes = "}}rognd}}}}ojef}epff}}nrf".getBytes();
        sufferedBytes = ByteStuffing.doStuffing(bytes);
        injectedBytes = ByteStuffing.inject(sufferedBytes);

        assertTrue(Arrays.equals(bytes, injectedBytes));

        bytes = "ro~~~nd}}~}}ojef}~epff}~}nrf}}}}}}}}".getBytes();
        sufferedBytes = ByteStuffing.doStuffing(bytes);
        injectedBytes = ByteStuffing.inject(sufferedBytes);

        assertTrue(Arrays.equals(bytes, injectedBytes));

        bytes = " dnwdnwkjndjwndnwkjdnwdkjw~~~~~~~~~~~~~}}}}}}}}}}}}}~~~~~~~~~~~}}}}}}}}}}".getBytes();
        sufferedBytes = ByteStuffing.doStuffing(bytes);
        injectedBytes = ByteStuffing.inject(sufferedBytes);

        assertTrue(Arrays.equals(bytes, injectedBytes));

    }
}
