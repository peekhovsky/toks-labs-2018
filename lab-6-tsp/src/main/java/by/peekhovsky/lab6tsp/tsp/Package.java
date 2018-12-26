package by.peekhovsky.lab6tsp.tsp;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;


/**
 * @author Rostislav Pekhovsky 2018
 * @version 0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@SuppressWarnings("WeakerAccess")
public class Package implements Comparable<Package> {

    /**
     * Sequence number.
     */
    private int sn;

    /**
     * Acknowledgment number.
     */
    private int an;

    /**
     * Checksum.
     */
    private int checkSum;

    /**
     * Message data.
     */
    private String message;

    /**
     * True if it is return package.
     */
    private boolean isReturn;

    @Override
    public int compareTo(@NotNull Package o) {
        return Integer.compare(this.sn, o.getSn());
    }
}
