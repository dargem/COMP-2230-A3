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
    private ArrayList<Instruction> instructions;
    private int instructionIndex = 0; // Essentially the instruction counter (what instruction runs next)
    private int endTime = 0;
    private final int PID;
    private final String name;

    ArrayList<Block> blocks = new ArrayList<>(); // Blocks this process has experienced, none should be null

    Process(ArrayList<Instruction> instructions, int PID, String name) {
        this.instructions = instructions;
        this.PID = PID;
        this.name = name;
    }

    // Check there is a next instruction to execute (if false process finished)
    boolean hasNextInstruction() {
        return instructionIndex < instructions.size();
    }

    // Get the next instruction to execute (check is safe using has next instruction)
    Instruction nextInstruction() {
        return instructions.get(instructionIndex++);
    }

    // Peek at the next instruction to execute (check is safe using has next instruction)
    Instruction peekNextInstruction() {
        return instructions.get(instructionIndex);
    }

    // Set the time this process has finished its operations
    void setEndTime(int time) {
        this.endTime = time;
    }

    // Get the time this process finishes
    int getEndTime() {
        return endTime;
    }

    // Returns the end of the last time this process was blocked
    // Throws an error if it has never been blocked
    int getUnblockTime() {
        return blocks.getLast().blockEnd();
    }

    // Returns the sequence number of the last block
    // if block A has a < sequence number than block B
    // this guarantees block A issued that block before block B did
    int getBlockSequence() {
        return blocks.getLast().sequenceNumber();
    }



    int getPID() {
        return PID;
    }

    // Add a block to the process for record keeping
    // A block should not be null
    void addBlock(Block block) {
        // Needed to keep logs of when page faults etc
        this.blocks.add(block);
    }

    String getName() {
        return name;
    }

    ArrayList<Block> getBlocks() {
        return blocks;
    }

}
