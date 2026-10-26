import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * A round robin scheduler for processes
 * Manages internally the ready and blocked processes
 *
 * @author Tristan Dyson
 * @date October 25th 2026
 * @version 1.1
 * @since 1.0
 */
public class Scheduler {
    private final Memory memory;
    private final ArrayDeque<Process> readyProcesses = new ArrayDeque<>();
    private final PriorityQueue<Process> blockedProcesses = new PriorityQueue<>(Comparator.comparingInt(Process::getUnblockTime));
    private final int timeQuantum;

    Scheduler(Memory memory, ArrayList<Process> processes, int timeQuantum) {
        this.memory = memory;
        for (var p : processes) readyProcesses.add(p);
        this.timeQuantum = timeQuantum;
    }

    void run() {
        int time = 0;
        Process runningProcess = null; // The currently running process, null if there isn'ts
        int quantumRemaining = timeQuantum; // The current running process' time quantum remaining

        while (!readyProcesses.isEmpty() && !blockedProcesses.isEmpty()) {

            while (blockedProcesses.peek().getUnblockTime() <= time) {
                // Add any blocked processes that have finished their wait to the ready queue
                readyProcesses.add(blockedProcesses.remove());
            }

            if (runningProcess != null && quantumRemaining == 0) {
                // Running process has finished
                readyProcesses.add(runningProcess);
            }

            if (runningProcess == null) {
                // Select a new process

                while (!readyProcesses.isEmpty()) {
                    var process = readyProcesses.remove();
                    if (process.hasNextInstruction()) {
                        runningProcess = process;
                        break;
                    }

                    // We have a ready process that finished its last instruction
                    var stat = memory.releaseMemory(process.getPID());
                    process.addOperationStatus(stat);

                    process.setEndTime(time + stat.timeTaken());
                }

                if (runningProcess == null) {
                    // We still haven't been able to select anything
                    continue;
                }
            }

            // We have a process to run here which hasn't finished yet
            var stat = memory.makeOperation(runningProcess.getPID(), runningProcess.nextInstruction());
            runningProcess.addOperationStatus(stat);

            if (stat.timeTaken() > 0) {
                // This means we have a delay (ie page fault)
                // So we need to place into the blocked queue until time taken elapses
                runningProcess.setUnblockTime(time + stat.timeTaken());
                blockedProcesses.add(runningProcess);
            } 
            // We only consider requeuing at the start 
            time++;
            quantumRemaining--;
        }
    }


    
}
