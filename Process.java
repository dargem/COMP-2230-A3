import java.util.ArrayList;

/**
 * Represents a process which is a series of instructions with accounting
 *
 * @author Tristan Dyson
 * @date October 23rd 2026
 * @version 1.0
 * @since 1.0
 */
public class Process {
    ArrayList<Instruction> instructions;
    int instructionIndex = 0;

    ArrayList<Integer> faultTimes;
}
