package by.peekhovsky.lab5tokenring.tokenring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.regex.Pattern;


/**
 * Entity class for token.
 *
 * @author Rostislav Pekhovsky
 * @version 0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    /**
     * Address tag.
     */
    private String addressTag;
    /**
     * Address tag.
     */
    private String returnAddressTag;
    /**
     * Message.
     */
    private String message;


}
