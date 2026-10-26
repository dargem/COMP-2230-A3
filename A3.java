import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Entrypoint to the virtual memory simulator
 * Reads from the provided file names configurations for each process
 * Then runs a simulation using them
 *
 * @author Tristan Dyson
 * @date October 26th 2026
 * @version 1.0
 * @since 1.0
 */

public class A3 {
    public static void main(String args[]) {
        var processes = new ArrayList<Process>();

        var numberFramesPerProcess = Integer.parseInt(args[0]);
        var timeQuantum = Integer.parseInt(args[1]);

        for (int i = 2; i < args.length; ++i) {
            processes.add(parseFile(args[i], i + 1));
        }

        var memory = new Memory(numberFramesPerProcess);
        var scheduler = new Scheduler(memory, processes, timeQuantum);

        scheduler.run();

        System.out.println("Clock - Fixed-Local Replacement:");
        System.out.printf(
            "%-12s %12s %4s %13s %s%n",
            "Process Name", "Turnaround Time", "# Faults", "# Write-Backs", "Fault Times"
        );

        for (var process : processes) {
            System.out.printf(
                "%-12s %12d %8d %14d %s%n",
                process.getName(),
                process.getEndTime(),
                process.getBlocks().size(),
                process.getBlocks().stream().filter(o -> o.didWriteBack()).count(),
                process.getBlocks().stream().map(o -> o.blockStart()).toArray()
            );
        }
    }

    // Parses a given filename, making a process using it
    public static Process parseFile(String name, int PID) {
        var instructions = new ArrayList<Instruction>();
        
        try {
            String content = Files.readString(Path.of(name));
            final var startIdx = content.indexOf("begin", 0) + "begin".length();
            final var endIdx = content.indexOf("end", 0);

            Operation rollingOperation = null;
            String rollingPageNumberAsString = ""; // Needs conversion into int 

            for (int currentIdx = startIdx; currentIdx <= endIdx; ++currentIdx) {
                // Essentially we roll from our start idx -> end idx 
                // We take note of when a new page request starts and finalize our rolling request

                var cur = content.charAt(currentIdx);
                switch (cur) {
                    case 'W', 'R', 'e' -> {
                        // We have finalized our current request (e included for trailing end)
                        if (rollingOperation != null) {
                            // Save the instruction
                            instructions.add(new Instruction(rollingOperation, Integer.parseInt(rollingPageNumberAsString)));
                        }

                        // Load new start in
                        if (cur == 'W') rollingOperation = Operation.WRITE;
                        else if (cur == 'R') rollingOperation = Operation.READ;
                    }
                    default -> {
                        if (Character.isDigit(cur)) {
                            if (cur >= '0' && cur <= '9') { 
                                // Additional guard for ASCII digits, probably not needed
                                rollingPageNumberAsString += cur;
                            }
                        }

                        // Whitespace we just skip
                    }
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File read exception, check the path is valid");
        }

        return new Process(instructions, PID, name);
    }
}
