package by.peekhovsky.lab5tokenring.tokenring;

import lombok.NonNull;

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
public final class TokenParser {

    /***
     * @param data data to parse
     * @return parsed list of tokens
     */
    public static List<Token> parse(@NonNull final String data) {
        List<Token> tokens = new ArrayList<>();
        try (Scanner scanner = new Scanner(data)) {
            while (scanner.hasNextLine()) {
                Optional<Token> tokenOptional = from(scanner.nextLine());
                tokenOptional.ifPresent(tokens::add);
            }
        }
        return tokens;
    }

    /***
     * @param tokens tokens to serialize
     * @return serialized token string
     */
    public static String serialize(@NonNull final List<Token> tokens) {
        StringBuilder builder = new StringBuilder();
        for (Token token : tokens) {
            builder
                    .append("\n")
                    .append("#to#")
                    .append(token.getAddressTag())
                    .append(" #from#")
                    .append(token.getReturnAddressTag())
                    .append(" ")
                    .append(token.getMessage());
        }
        return builder.toString();
    }

    /**
     * @param line string with data to make token object
     * @return optional of token
     */
    public static Optional<Token> from(@NonNull final String line) {
        Pattern patternAddress = Pattern.compile("#to#[a-zA-Z_0-9]+");
        Pattern patternReturnAddress = Pattern.compile("#from#[a-zA-Z_0-9]+");
        Token token = new Token();
        Optional<Token> tokenOptional;
        try (Scanner scanner = new Scanner(line)) {

            String addressTagTemp = scanner.next(patternAddress);
            if (addressTagTemp.length() > 2) {
                token.setAddressTag(addressTagTemp
                        .replace("#to#", ""));
            } else {
                token.setAddressTag("undefined");
            }

            String returnAddressTagTemp = scanner.next(patternReturnAddress);
            if (returnAddressTagTemp.length() > 2) {
                token.setReturnAddressTag(
                        returnAddressTagTemp
                                .replace("#from#", ""));
            } else {
                token.setAddressTag("undefined");
            }

            token.setMessage(scanner.nextLine());
            tokenOptional = Optional.of(token);

        } catch (NoSuchElementException e) {
            tokenOptional = Optional.empty();
        }
        return tokenOptional;
    }

    /***
     * Private no args constructor.
     */
    private TokenParser() {
    }

}
