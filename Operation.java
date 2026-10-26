import java.util.InputMismatchException;

/**
 * Represents an operation on memory, can be a read or write
 *
 * @author Tristan Dyson
 * @date October 23rd 2026
 * @version 1.0
 * @since 1.0
 */

public enum Operation {
    WRITE,
    READ;

    public static Operation fromString(String s) {
        return switch(s) {
            case "W" -> Operation.WRITE;
            case "R" -> Operation.READ;
            default -> {
                throw new InputMismatchException("String not recognised for operation conversion");
            }
        };
    }
}
