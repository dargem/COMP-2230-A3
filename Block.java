/**
 * Is the status for a memory operation
 *
 * @author Tristan Dyson
 * @date October 23rd 2026
 * @version 1.1
 * @since 1.0
 */
public record Block (int blockStart, int blockEnd, boolean didPageFault, boolean didWriteBack) {}
