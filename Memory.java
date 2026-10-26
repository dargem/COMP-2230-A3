import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Memory represents a computer's main memory
 * It internally handles what pages process' have in memory
 * It uses a clock replacement algorithm for swapping out pages if a page fault occurs
 *
 * @author Tristan Dyson
 * @date October 23rd 2026
 * @version 1.2
 * @since 1.0
 */
public class Memory {

    // Is a page with accounting info
    private class Page {
        int pageNumber;

        // If true this page is dirty (different from main memory) needs writing back
        // If false then it is clean and doesn't need a write back
        boolean dirtyBit;

        // The clock bit used for the clock replacement algorithm
        // 1 = True, 0 = False
        boolean clockBit; // Or perhaps byte not bit I should say for both of these...

        Page(int pageNumber) {
            this.pageNumber = pageNumber;
            this.dirtyBit = false; // Just putting it into memory not written
            this.clockBit = true; // Bit should be raised initially
        }

        // Effectively reconstruct the object to avoid issues with modifying vs reseating reference
        void swap(int pageNumber) {
            this.pageNumber = pageNumber;
            this.dirtyBit = false; // Just putting it into memory not written
            this.clockBit = true; // Bit should be raised initially
        }
    }

    // A process can have up to 50 entries max in memory
    final int maxEntries;
    final static int PAGE_FAULT_TIME_TAKEN = 4;
    final static int DIRTY_WRITE_BACK_TIME_TAKEN = 1;

    static int blockSequenceNumber = 0; // increasing sequence number, incremented each time a block produced

    Memory(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    // Linked hashmap of pagetable entries in main memory, Integer is page no, value is the metadata associated with a page
    // Must be smaller in size than MAX_ENTRIES, the iterator produced by a linked hash map maintains insertion order

    // Index into the hashmap using PID to get pages in memory for that process
    // containership in linked hash map checks for if that page exists in memory, and index into it to get metadata 
    HashMap<Integer, ArrayList<Page>> inMemoryProcessPages = new LinkedHashMap<>();

    // For the clock replacement policy 
    HashMap<Integer, CyclicArrayListIterator<Page, ArrayList<Page>>> clockPageIterators = new HashMap<>(); 

    // Make a memory operation on a given processes page number
    // Returns a Block for if the operation blocks (null if it doesn't)
    Block makeOperation(int PID, Instruction instruction, int time) {
        int timeTaken = 0; // With each condition which can cause a delay add to this
        boolean didPageFault = false;
        boolean didWriteBack = false;
        var pageNumber = instruction.pageNumber();
        var op = instruction.op();

        inMemoryProcessPages.putIfAbsent(PID, new ArrayList<>());
        clockPageIterators.putIfAbsent(PID, new CyclicArrayListIterator<>(inMemoryProcessPages.get(PID)));

        var inMemoryPages = inMemoryProcessPages.get(PID);


        if (inMemoryPages.stream().noneMatch(page -> page.pageNumber == pageNumber)) {
            // Page fault, we need to add this page into memory in order to do our op
            timeTaken += PAGE_FAULT_TIME_TAKEN;
            didPageFault = true;

            if (inMemoryPages.size() < maxEntries) {
                inMemoryPages.add(new Page(pageNumber));
            }
            else {
                var iter = clockPageIterators.get(PID);

                while (true) {
                    var page = iter.next();
                    if (page.clockBit) {
                        // If set (1) unset (0), java does not let let 0/1 be implicitly convertible to bool...
                        page.clockBit = false;
                    }
                    else {
                        if (page.dirtyBit) {
                            timeTaken += DIRTY_WRITE_BACK_TIME_TAKEN;
                            didWriteBack = true;
                        }
                        page.swap(pageNumber);
                        break;
                    }
                }
            }
        }

        Page p = inMemoryPages.stream()
            .filter(page -> page.pageNumber == pageNumber) 
            .findFirst().orElseThrow(); // We know it must be in memory (since we just added it...)
        
        if (op == Operation.WRITE) {
            p.dirtyBit = true;
        }

        p.clockBit = true;
        
        return (didPageFault || didWriteBack) 
            ? new Block(time, time + timeTaken, didPageFault, didWriteBack, blockSequenceNumber++) 
            : null; 
    }

    // Release all the memory associated with a PID
    // Returns a Block if it blocked, if it didn't block this is null
    Block releaseMemory(int PID, int time) {
        boolean hasDirtyPage = false;
        var mem = inMemoryProcessPages.get(PID);
        for (var p : mem) {
            if (!p.dirtyBit) continue;

            hasDirtyPage = true;
            break;
        }
        inMemoryProcessPages.put(PID, new ArrayList<>()); // Wipes it
        return hasDirtyPage 
            ? new Block(time, time + DIRTY_WRITE_BACK_TIME_TAKEN, false, hasDirtyPage, blockSequenceNumber++)
            : null;
    }
}