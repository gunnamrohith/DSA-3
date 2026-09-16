package interview.exam;

import java.util.*;

/**
 * Candidate 4: Independent Implementation
 * Solved the exam question using an alternate balanced structure (Red-Black / TreeMap backed dictionary).
 */
public class DictionarySearchEngine {

    private final TreeSet<Integer> set;

    public DictionarySearchEngine() {
        this.set = new TreeSet<>();
    }

    public void addElement(int number) {
        set.add(number);
    }

    public boolean hasElement(int number) {
        return set.contains(number);
    }

    public void displayAll() {
        for (int val : set) {
            System.out.print(val + " -> ");
        }
        System.out.println("END");
    }

    public int findClosest(int target) {
        Integer floor = set.floor(target);
        Integer ceil = set.ceiling(target);
        if (floor == null) return (ceil == null) ? -1 : ceil;
        if (ceil == null) return floor;
        return (target - floor <= ceil - target) ? floor : ceil;
    }
}
