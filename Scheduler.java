import java.util.ArrayList;

/**
 * A round robin scheduler for processes
 * Manages internally the ready, blocked, and to be added processes
 *
 * @author Tristan Dyson
 * @date October 25th 2026
 * @version 1.0
 * @since 1.0
 */
public class Scheduler {
    private final Memory memory;
    private final ArrayList<Process> processes;

    Scheduler(Memory memory, ArrayList<Process> processes) {
        this.memory = memory;
        this.processes = processes;
    }

    
}
