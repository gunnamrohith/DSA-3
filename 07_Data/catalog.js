const STUDENT_PROFILES = [
  { id: 'S01', name: 'Student 01', style: 'Reference structure' },
  { id: 'S02', name: 'Student 02', style: 'Renamed variables' },
  { id: 'S03', name: 'Student 03', style: 'Compact formatting' },
  { id: 'S04', name: 'Student 04', style: 'Commented structure' }
];

const ALGORITHM_DEFINITIONS = [
  ['bubble-sort', 'Bubble Sort', 'Sorting', `public class BubbleSort {
    public static void sort(int[] values) {
        for (int end = values.length - 1; end > 0; end--) {
            for (int index = 0; index < end; index++) {
                if (values[index] > values[index + 1]) {
                    int temp = values[index]; values[index] = values[index + 1]; values[index + 1] = temp;
                }
            }
        }
    }
}`],
  ['selection-sort', 'Selection Sort', 'Sorting', `public class SelectionSort {
    public static void sort(int[] values) {
        for (int index = 0; index < values.length - 1; index++) {
            int minimum = index;
            for (int scan = index + 1; scan < values.length; scan++) if (values[scan] < values[minimum]) minimum = scan;
            int temp = values[index]; values[index] = values[minimum]; values[minimum] = temp;
        }
    }
}`],
  ['insertion-sort', 'Insertion Sort', 'Sorting', `public class InsertionSort {
    public static void sort(int[] values) {
        for (int index = 1; index < values.length; index++) {
            int key = values[index], scan = index - 1;
            while (scan >= 0 && values[scan] > key) { values[scan + 1] = values[scan]; scan--; }
            values[scan + 1] = key;
        }
    }
}`],
  ['merge-sort', 'Merge Sort', 'Sorting', `public class MergeSort {
    public static void sort(int[] values, int left, int right) {
        if (left >= right) return;
        int middle = (left + right) / 2; sort(values, left, middle); sort(values, middle + 1, right);
        int[] copy = values.clone(); int first = left, second = middle + 1;
        for (int index = left; index <= right; index++) values[index] = first > middle ? copy[second++] : second > right ? copy[first++] : copy[first] <= copy[second] ? copy[first++] : copy[second++];
    }
}`],
  ['quick-sort', 'Quick Sort', 'Sorting', `public class QuickSort {
    public static void sort(int[] values, int low, int high) {
        if (low >= high) return; int pivot = values[high], boundary = low;
        for (int index = low; index < high; index++) if (values[index] <= pivot) { int temp = values[index]; values[index] = values[boundary]; values[boundary++] = temp; }
        int temp = values[boundary]; values[boundary] = values[high]; values[high] = temp;
        sort(values, low, boundary - 1); sort(values, boundary + 1, high);
    }
}`],
  ['heap-sort', 'Heap Sort', 'Sorting', `public class HeapSort {
    public static void sort(int[] values) {
        for (int index = values.length / 2 - 1; index >= 0; index--) heapify(values, values.length, index);
        for (int end = values.length - 1; end > 0; end--) { int temp = values[0]; values[0] = values[end]; values[end] = temp; heapify(values, end, 0); }
    }
    private static void heapify(int[] values, int size, int root) { int largest = root, left = root * 2 + 1, right = left + 1; if (left < size && values[left] > values[largest]) largest = left; if (right < size && values[right] > values[largest]) largest = right; if (largest != root) { int temp = values[root]; values[root] = values[largest]; values[largest] = temp; heapify(values, size, largest); } }
}`],

  ['linear-search', 'Linear Search', 'Searching', `public class LinearSearch {
    public static int search(int[] values, int target) {
        for (int index = 0; index < values.length; index++) if (values[index] == target) return index;
        return -1;
    }
}`],
  ['binary-search', 'Binary Search', 'Searching', `public class BinarySearch {
    public static int search(int[] values, int target) {
        int low = 0, high = values.length - 1;
        while (low <= high) { int middle = low + (high - low) / 2; if (values[middle] == target) return middle; if (values[middle] < target) low = middle + 1; else high = middle - 1; }
        return -1;
    }
}`],
  ['jump-search', 'Jump Search', 'Searching', `public class JumpSearch {
    public static int search(int[] values, int target) {
        int step = (int) Math.sqrt(values.length), previous = 0;
        while (previous < values.length && values[Math.min(step, values.length) - 1] < target) { previous = step; step += (int) Math.sqrt(values.length); }
        while (previous < Math.min(step, values.length) && values[previous] < target) previous++;
        return previous < values.length && values[previous] == target ? previous : -1;
    }
}`],
  ['exponential-search', 'Exponential Search', 'Searching', `public class ExponentialSearch {
    public static int search(int[] values, int target) {
        if (values.length == 0 || values[0] == target) return values.length == 0 ? -1 : 0;
        int bound = 1; while (bound < values.length && values[bound] < target) bound *= 2;
        int low = bound / 2, high = Math.min(bound, values.length - 1);
        while (low <= high) { int middle = (low + high) / 2; if (values[middle] == target) return middle; if (values[middle] < target) low = middle + 1; else high = middle - 1; } return -1;
    }
}`],
  ['ternary-search', 'Ternary Search', 'Searching', `public class TernarySearch {
    public static int search(int[] values, int target) {
        int low = 0, high = values.length - 1;
        while (low <= high) { int third = (high - low) / 3, first = low + third, second = high - third; if (values[first] == target) return first; if (values[second] == target) return second; if (target < values[first]) high = first - 1; else if (target > values[second]) low = second + 1; else { low = first + 1; high = second - 1; } } return -1;
    }
}`],
  ['interpolation-search', 'Interpolation Search', 'Searching', `public class InterpolationSearch {
    public static int search(int[] values, int target) {
        int low = 0, high = values.length - 1;
        while (low <= high && target >= values[low] && target <= values[high]) { if (values[low] == values[high]) return values[low] == target ? low : -1; int position = low + (int) (((long) (target - values[low]) * (high - low)) / (values[high] - values[low])); if (values[position] == target) return position; if (values[position] < target) low = position + 1; else high = position - 1; } return -1;
    }
}`],

  ['kadanes-algorithm', "Kadane's Algorithm", 'Arrays', `public class KadanesAlgorithm {
    public static int maximumSum(int[] values) {
        int best = values[0], current = values[0];
        for (int index = 1; index < values.length; index++) { current = Math.max(values[index], current + values[index]); best = Math.max(best, current); }
        return best;
    }
}`],
  ['two-sum', 'Two Sum', 'Arrays', `import java.util.*;
public class TwoSum {
    public static int[] find(int[] values, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int index = 0; index < values.length; index++) { int need = target - values[index]; if (seen.containsKey(need)) return new int[]{seen.get(need), index}; seen.put(values[index], index); }
        return new int[]{-1, -1};
    }
}`],
  ['prefix-sum', 'Prefix Sum Queries', 'Arrays', `public class PrefixSumQueries {
    public static int rangeSum(int[] values, int left, int right) {
        int[] prefix = new int[values.length + 1];
        for (int index = 0; index < values.length; index++) prefix[index + 1] = prefix[index] + values[index];
        return prefix[right + 1] - prefix[left];
    }
}`],
  ['sliding-window', 'Maximum Sliding Window', 'Arrays', `import java.util.*;
public class MaximumSlidingWindow {
    public static int[] maximums(int[] values, int width) {
        Deque<Integer> deque = new ArrayDeque<>(); int[] result = new int[values.length - width + 1];
        for (int index = 0; index < values.length; index++) { while (!deque.isEmpty() && deque.peekFirst() <= index - width) deque.removeFirst(); while (!deque.isEmpty() && values[deque.peekLast()] <= values[index]) deque.removeLast(); deque.addLast(index); if (index >= width - 1) result[index - width + 1] = values[deque.peekFirst()]; }
        return result;
    }
}`],
  ['rotate-array', 'Rotate Array', 'Arrays', `public class RotateArray {
    public static void rotate(int[] values, int steps) {
        steps %= values.length; reverse(values, 0, values.length - 1); reverse(values, 0, steps - 1); reverse(values, steps, values.length - 1);
    }
    private static void reverse(int[] values, int left, int right) { while (left < right) { int temp = values[left]; values[left++] = values[right]; values[right--] = temp; } }
}`],
  ['dutch-flag', 'Dutch National Flag', 'Arrays', `public class DutchNationalFlag {
    public static void sortColors(int[] values) {
        int low = 0, index = 0, high = values.length - 1;
        while (index <= high) { if (values[index] == 0) { int temp = values[low]; values[low++] = values[index]; values[index++] = temp; } else if (values[index] == 2) { int temp = values[high]; values[high--] = values[index]; values[index] = temp; } else index++; }
    }
}`],

  ['fibonacci-dp', 'Fibonacci DP', 'Dynamic Programming', `public class FibonacciDP {
    public static long fibonacci(int number) {
        if (number < 2) return number; long previous = 0, current = 1;
        for (int index = 2; index <= number; index++) { long next = previous + current; previous = current; current = next; }
        return current;
    }
}`],
  ['climbing-stairs', 'Climbing Stairs', 'Dynamic Programming', `public class ClimbingStairs {
    public static int countWays(int steps) {
        if (steps <= 2) return steps; int previous = 1, current = 2;
        for (int step = 3; step <= steps; step++) { int next = previous + current; previous = current; current = next; }
        return current;
    }
}`],
  ['coin-change', 'Coin Change', 'Dynamic Programming', `import java.util.*;
public class CoinChange {
    public static int minimumCoins(int[] coins, int amount) {
        int[] best = new int[amount + 1]; Arrays.fill(best, amount + 1); best[0] = 0;
        for (int value = 1; value <= amount; value++) for (int coin : coins) if (coin <= value) best[value] = Math.min(best[value], best[value - coin] + 1);
        return best[amount] > amount ? -1 : best[amount];
    }
}`],
  ['knapsack', '0/1 Knapsack', 'Dynamic Programming', `public class Knapsack {
    public static int maximumValue(int[] weights, int[] values, int capacity) {
        int[] best = new int[capacity + 1];
        for (int item = 0; item < weights.length; item++) for (int space = capacity; space >= weights[item]; space--) best[space] = Math.max(best[space], best[space - weights[item]] + values[item]);
        return best[capacity];
    }
}`],
  ['longest-common-subsequence', 'Longest Common Subsequence', 'Dynamic Programming', `public class LongestCommonSubsequence {
    public static int length(String first, String second) {
        int[][] table = new int[first.length() + 1][second.length() + 1];
        for (int row = 1; row <= first.length(); row++) for (int column = 1; column <= second.length(); column++) table[row][column] = first.charAt(row - 1) == second.charAt(column - 1) ? table[row - 1][column - 1] + 1 : Math.max(table[row - 1][column], table[row][column - 1]);
        return table[first.length()][second.length()];
    }
}`],
  ['longest-increasing-subsequence', 'Longest Increasing Subsequence', 'Dynamic Programming', `import java.util.*;
public class LongestIncreasingSubsequence {
    public static int length(int[] values) {
        int[] tails = new int[values.length]; int size = 0;
        for (int value : values) { int position = Arrays.binarySearch(tails, 0, size, value); if (position < 0) position = -position - 1; tails[position] = value; if (position == size) size++; }
        return size;
    }
}`],
  ['edit-distance', 'Edit Distance', 'Dynamic Programming', `public class EditDistance {
    public static int distance(String first, String second) {
        int[][] table = new int[first.length() + 1][second.length() + 1];
        for (int row = 0; row <= first.length(); row++) table[row][0] = row; for (int column = 0; column <= second.length(); column++) table[0][column] = column;
        for (int row = 1; row <= first.length(); row++) for (int column = 1; column <= second.length(); column++) table[row][column] = first.charAt(row - 1) == second.charAt(column - 1) ? table[row - 1][column - 1] : 1 + Math.min(table[row - 1][column - 1], Math.min(table[row - 1][column], table[row][column - 1]));
        return table[first.length()][second.length()];
    }
}`]
];

function createStudentVariant(source, profileIndex) {
  if (profileIndex === 0) return source;

  let variant = source
    .replaceAll('values', profileIndex === 1 ? 'numbers' : 'data')
    .replaceAll('index', profileIndex === 1 ? 'position' : 'i')
    .replaceAll('target', profileIndex === 1 ? 'key' : 'goal')
    .replaceAll('current', profileIndex === 1 ? 'running' : 'active')
    .replaceAll('previous', profileIndex === 1 ? 'prior' : 'before')
    .replace(/public class (\w+)/, (_, className) => `public class ${className}Student${profileIndex + 1}`);

  if (profileIndex === 2) {
    variant = variant.replace(/\n\s*/g, ' ').replace(/\s+/g, ' ').replace(/ \}/g, '\n}');
  }
  if (profileIndex === 3) {
    variant = variant.replace('{\n', '{\n    // Student implementation using the selected DSA method\n');
  }
  return variant;
}

const CODE_CATALOG = ALGORITHM_DEFINITIONS.flatMap(([id, name, category, source]) =>
  STUDENT_PROFILES.map((student, profileIndex) => ({
    id: `${id}-${student.id.toLowerCase()}`,
    algorithmId: id,
    algorithm: name,
    category,
    studentId: student.id,
    studentName: student.name,
    style: student.style,
    code: createStudentVariant(source, profileIndex)
  }))
);

const catalogIds = new Set(CODE_CATALOG.map(item => item.id));
if (CODE_CATALOG.length !== 100 || catalogIds.size !== CODE_CATALOG.length) {
  throw new Error('The predefined exam catalog must contain exactly 100 unique submissions.');
}