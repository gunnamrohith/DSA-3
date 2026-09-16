package interview.exam;

// Obfuscated copy: Variable names changed, comments replaced, braces styled differently
public class CustomBSTStorage {

    static class TreeNode {
        int val;
        TreeNode leftChild, rightChild;

        public TreeNode(int v) {
            val = v;
            leftChild = rightChild = null;
        }
    }

    TreeNode treeHead;

    public CustomBSTStorage() {
        treeHead = null;
    }

    // Insert new element into BST
    public void addValue(int v) {
        treeHead = addRecursive(treeHead, v);
    }

    private TreeNode addRecursive(TreeNode current, int v) {
        if (current == null) {
            current = new TreeNode(v);
            return current;
        }
        if (v < current.val) {
            current.leftChild = addRecursive(current.leftChild, v);
        } else if (v > current.val) {
            current.rightChild = addRecursive(current.rightChild, v);
        }
        return current;
    }

    // Check if key exists
    public boolean contains(int v) {
        return findRecursive(treeHead, v);
    }

    private boolean findRecursive(TreeNode current, int v) {
        if (current == null) return false;
        if (current.val == v) return true;
        if (current.val < v) {
            return findRecursive(current.rightChild, v);
        }
        return findRecursive(current.leftChild, v);
    }

    // Print sorted keys
    public void printSorted() {
        traverse(treeHead);
        System.out.println();
    }

    private void traverse(TreeNode current) {
        if (current != null) {
            traverse(current.leftChild);
            System.out.print(current.val + " ");
            traverse(current.rightChild);
        }
    }
}
