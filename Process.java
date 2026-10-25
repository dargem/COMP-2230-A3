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
    private Integer unblockTime = null; // Null if this processes isn't currently blocked 
    private int endTime = -1;
    private final int PID;

    ArrayList<OperationStatus> operationStatuses;

    Process(ArrayList<Instruction> instructions, int PID) {
        this.instructions = instructions;
        this.PID = PID;
    }

    // Check there is a next instruction to execute (if false process finished)
    boolean hasNextInstruction() {
        return instructionIndex < instructions.size();
    }

    // Get the next instruction to execute (check is safe using has next instruction)
    Instruction nextInstruction() {
        return instructions.get(instructionIndex++);
    }

    // Set the time this process has finished its operations
    void setEndTime(int time) {
        this.endTime = time;
    }

    int getEndTime() {
        return endTime;
    }

    Integer getUnblockTime() {
        return unblockTime;
    }

    void setUnblockTime(int unblockTime) {
        this.unblockTime = unblockTime;
    }

    int getPID() {
        return PID;
    }

    // Add the status of an operation to the process
    void addOperationStatus(OperationStatus opStat) {
        // Needed to keep logs of when page faults etc
        this.operationStatuses.add(opStat);
    }

}
