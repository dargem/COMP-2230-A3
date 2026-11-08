import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

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
        for (int i = 2; i < args.length; ++i) {
            processes.add(parseFile(args[i], i - 1)); // Start PID at 1
        }

        var numberFramesPerProcess = Integer.parseInt(args[0]) / processes.size(); // total frames / frames per process, java floors it
        var timeQuantum = Integer.parseInt(args[1]);

        var memory = new Memory(numberFramesPerProcess);
        var scheduler = new Scheduler(memory, processes, timeQuantum);

        scheduler.run();

        System.out.println("Clock - Fixed-Local Replacement:");
        String header = "%-18s%-17s%-10s%-15s%s%n";
        String row    = "%-18s%-17d%-10d%-15d%s%n";
    
        System.out.printf(header, "Process Name", "Turnaround Time", "# Faults", "# Write-Backs", "Fault Times");

        for (var process : processes) {
            var blocks = process.getBlocks();

            String faultTimes = blocks.stream()
                .filter(o -> o.didPageFault())
                .map(o -> String.valueOf(o.blockStart()))
                .collect(java.util.stream.Collectors.joining(", ", "{", "}"));

            System.out.printf(
                row,
                process.getName(),
                process.getEndTime(),
                blocks.stream().filter(o -> o.didPageFault()).count(),
                blocks.stream().filter(o -> o.didWriteBack()).count(),
                faultTimes
            );
        }
    }

    // Parses a given filename, making a process using it
    public static Process parseFile(String name, int PID) {
        var instructions = new ArrayList<Instruction>();

        try (Scanner scanner = new Scanner(Path.of(name))) {
            while (scanner.hasNext()) {
                if (scanner.next().equalsIgnoreCase("begin")) break;
            }

            while (scanner.hasNext()) {
                String opToken = scanner.next();
                if (opToken.equalsIgnoreCase("end")) break;

                Operation op = Operation.fromString(opToken);
                int page = scanner.nextInt();
                instructions.add(new Instruction(op, page));
            }
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0); // If there's any exception file is not valid so quit
        }

        return new Process(instructions, PID, "Process" + PID);
    }
}
