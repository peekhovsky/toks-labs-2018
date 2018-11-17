package by.peekhovksy.lab5tokenring;

import by.peekhovsky.lab5tokenring.tokenring.Token;
import by.peekhovsky.lab5tokenring.tokenring.TokenParser;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestTokenFrom {

    @DataProvider
    public Object[][] parseTokenFrom() {
        return new Object[][] {
                {
                    "#to#aaabbb #from#cccddd 4t4t",
                        new Token("aaabbb", "cccddd", "4t4t")
                },
                {
                        "#aaabbb#",
                        null
                },
                {
                        "##",
                        null
                },
        };
    }

    @Test(dataProvider = "parseTokenFrom")
    public void testTokenFrom(String str, Token expectedToken) {
        Optional<Token> tokenOptional = TokenParser.from(str);
        assertEquals(tokenOptional.orElse(null), expectedToken);
    }

    @Test
    public void testParse() {
        String str = "\n#to#aaa #from#bbb message"
                   + "\n#to#ccee #from#dfd dvdvvd"
                   + "\n#to#efeefef #from#d ytj5"
                   + "\n#to#efefeff #from#dv rtjt645"
                   + "\n#to#2wwew #from#vdvddv t"
                   + "\ndwdw dwdw wd d wdw wd";
        List<Token> actualList = new ArrayList<>();
        actualList.add(new Token("aaa", "bbb", "message"));
        actualList.add(new Token("ccee", "dfd", "dvdvvd"));
        actualList.add(new Token("efeefef", "d", "ytj5"));
        actualList.add(new Token("efefeff", "dv", "rtjt645"));
        actualList.add(new Token("2wwew", "vdvddv", "t"));

        assertEquals(TokenParser.parse(str), actualList);
    }

    @Test
    public void testSerialize() {
        List<Token> list = new ArrayList<>();
        list.add(new Token("aaa", "bbb", "message"));
        list.add(new Token("ccee", "dfd", "dvdvvd"));
        list.add(new Token("efeefef", "d", "ytj5"));
        list.add(new Token("efefeff", "dv", "rtjt645"));
        list.add(new Token("2wwew", "vdvddv", "t"));

        String actualStr = "\n#to#aaa #from#bbb message"
                + "\n#to#ccee #from#dfd dvdvvd"
                + "\n#to#efeefef #from#d ytj5"
                + "\n#to#efefeff #from#dv rtjt645"
                + "\n#to#2wwew #from#vdvddv t";

        assertEquals(TokenParser.serialize(list), actualStr);
    }
}
