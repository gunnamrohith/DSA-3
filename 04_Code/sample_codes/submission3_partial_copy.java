package interview.exam;

import java.util.*;

/**
 * Candidate 3: Partial Plagiarism
 * Copied the insertRec logic but rewrote search iteratively and added AVL height balancing logic.
 */
public class MixedTreeAnalyzer {

    static class Node {
        int key;
        Node left, right;
        int height;

        public Node(int item) {
            key = item;
            height = 1;
        }
    }

    private Node root;

    // Direct verbatim copy of Candidate 1's recursion
    private Node insertRec(Node root, int key) {
        if (root == null) {
            root = new Node(key);
            return root;
        }
        if (key < root.key) {
            root.left = insertRec(root.left, key);
        } else if (key > root.key) {
            root.right = insertRec(root.right, key);
        }
        return root;
    }

    // Completely independent iterative search implementation
    public boolean iterativeSearch(int target) {
        Node current = root;
        while (current != null) {
            if (current.key == target) return true;
            current = (target < current.key) ? current.left : current.right;
        }
        return false;
    }

    public List<Integer> levelOrder() {
        List<Integer> list = new ArrayList<>();
        if (root == null) return list;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node temp = queue.poll();
            list.add(temp.key);
            if (temp.left != null) queue.add(temp.left);
            if (temp.right != null) queue.add(temp.right);
        }
        return list;
    }
}
