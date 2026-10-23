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
 * @version 1.1
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
    }

    // A process can have up to 50 entries max in memory
    final static int MAX_ENTRIES = 50;
    final static int PAGE_FAULT_TIME_TAKEN = 4;
    final static int DIRTY_WRITE_BACK_TIME_TAKEN = 1;

    // Linked hashmap of pagetable entries in main memory, Integer is page no, value is the metadata associated with a page
    // Must be smaller in size than MAX_ENTRIES, the iterator produced by a linked hash map maintains insertion order

    // Index into the hashmap using PID to get pages in memory for that process
    // containership in linked hash map checks for if that page exists in memory, and index into it to get metadata 
    HashMap<Integer, ArrayList<Page>> inMemoryPages = new LinkedHashMap<>();

    int makeWrite(int PID, int pageNumber) {
        int timeTaken = 0; // With each condition which can cause a delay add to this

        inMemoryPages.putIfAbsent(PID, new ArrayList<>());

        var pages = inMemoryPages.get(PID);

        
        if (!pages.containsKey(pageNumber)) {
            // Page fault, we need to add this page into memory in order to do our op

            if (pages.size() < MAX_ENTRIES) {
                // We can add an entry into memory to resolve this
                pages.put(pageNumber, new Metadata());
            }
            else {
                // We need to replace an entry in memory to resolve this
                // We are using a clock replacement policy
                pages.
            } 
        }

        return 4;
    }


}