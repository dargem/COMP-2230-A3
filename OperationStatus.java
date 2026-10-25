/**
 * Is the status for a memory operation
 *
 * @author Tristan Dyson
 * @date October 23rd 2026
 * @version 1.0
 * @since 1.0
 */
public record OperationStatus (int timeTaken, boolean didPageFault, boolean didWriteBack) {}
