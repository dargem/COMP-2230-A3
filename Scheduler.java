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
    private final PriorityQueue<Process> blockedProcesses = new PriorityQueue<>(Comparator.comparingInt(Process::getUnblockTime).thenComparingInt(Process::getBlockSequence));
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

        while (!readyProcesses.isEmpty() || !blockedProcesses.isEmpty() || runningProcess != null) {

            while (blockedProcesses.size() > 0 && blockedProcesses.peek().getUnblockTime() <= time) {
                // Add any blocked processes that have finished their wait to the ready queue
                readyProcesses.add(blockedProcesses.remove());
            }

            if (runningProcess != null && !runningProcess.hasNextInstruction()) {
                // Running process has completed its instructions
                var block = memory.releaseMemory(runningProcess.getPID(), time);
                if (block != null) {
                    runningProcess.addBlock(block);
                    runningProcess.setEndTime(block.blockEnd());
                }
                else {
                    runningProcess.setEndTime(time);
                }

                runningProcess = null;
                quantumRemaining = timeQuantum;
            }

            if (runningProcess != null && quantumRemaining == 0) {
                // Running process has finished its time quantum
                readyProcesses.add(runningProcess);
                runningProcess = null;
                quantumRemaining = timeQuantum;
            }

            if (runningProcess == null) {
                // Select a new process

                while (!readyProcesses.isEmpty()) {
                    var process = readyProcesses.remove();
                    if (process.hasNextInstruction()) {
                        runningProcess = process;
                        quantumRemaining = timeQuantum;
                        break;
                    }

                    // We have a ready process that finished its last instruction
                    var block = memory.releaseMemory(process.getPID(), time);
                    if (block != null) {
                        process.addBlock(block);
                        process.setEndTime(block.blockEnd());
                        continue;
                    }

                    process.setEndTime(time);
                }

                if (runningProcess == null) {
                    // We still haven't been able to select anything
                    
                    time++;
                    continue;
                }
            }

            // We have a process to run here which hasn't finished yet
            var block = memory.makeOperation(runningProcess.getPID(), runningProcess.peekNextInstruction(), time);
            if (block != null) {
                // This means we have a delay (ie page fault)
                // So we need to place into the blocked queue until time taken elapses
                runningProcess.addBlock(block);
                blockedProcesses.add(runningProcess);
                runningProcess = null;
            }
            else {
                // We only consider requeuing at the start, execute the instruction
                runningProcess.nextInstruction();
                time++;
                quantumRemaining--;
            }
        }
    }


    
}
