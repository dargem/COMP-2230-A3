/**
 * Represents a page in memory with accounting information
 *
 * @author Tristan Dyson
 * @date October 29th 2026
 * @version 1.0
 * @since 1.0
 */
public class Page {
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