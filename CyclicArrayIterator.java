import java.util.Iterator;
import java.util.ArrayList;

/**
 * An iterator for an array of a provided type
 * It is unique in that it "wraps around" silently and is stable to push_back operations
 * It is not safe if there are structural modifications that aren't pushing to the back
 * This guarantees if you have an array [0, 1], then iterate over it calling next twice,
 * Then push 2 to the back so you have [0, 1, 2] the next .next() will return 2
 *
 * @author Tristan Dyson
 * @date October 23rd 2026
 * @version 1.0
 * @since 1.0
 */
public class CyclicArrayIterator<E, T extends ArrayList<E>> implements Iterator<E> {
    T list;
    int idx; // Our current index in the array

    // List is the provided container to iterate over
    CyclicArrayIterator(T list) {
        this.list = list;
        this.idx = -1; // This is to avoid a bug with iterator wraparound, see details in next()
    }

    @Override
    public boolean hasNext() {
        return list.size() != 0; // Will always have next if its not empty
    }

    @Override
    public E next() {
        // We actually want to increment index before we return the element
        // This is because for example there are elements [0, 1, 2]
        // The iterator returns element 2 and index would be 0 if we wrapped around after
        // But then if the user pushes [3] we would be indexing into 0 next and not 3 which breaks this
        // To avoid any starting bugs idx is initialized with -1 so there is no starting edge case
        idx = (idx + 1) % list.size();
        return list.get(idx);
    }
    
}
