package oop.ex4.data_structures;
import java.util.LinkedList;

/**
 *An implementation of the AVL tree data structure.
 */
public class AvlTree implements Iterable<Integer> {
    AVLNode root;
    int amountOfNodes;
    LinkedList<Integer> avlList = new LinkedList<>();
    static final double HEIGHTSQRT = Math.sqrt(5);
    final int RIGHTUNBALANCED = 2;
    final int LEFTUNBALANCED = -2;
    final int UNFOUND = -1;

    /**
     * A nested class to act as an AVL Node that dwell with in the tree (as it's leaves).
     */
    private static class AVLNode {
        private AVLNode left;
        private AVLNode right;
        private AVLNode parent;
        private int value;
        private  int balance;
        private int height;

        AVLNode(int value) {
            this.value = value;
            this.parent = null;
            this.left = null;
            this.right = null;
        }
    }

    /**
     * The default AVL constructor.
     */
    public AvlTree() {
        root = null;
    }
    /**
     * A copy constructor that creates a deep copy of the given AvlTree.
     * @param tree - The AVL tree to be copied.
     */
    public AvlTree(AvlTree tree) {
        for(int value: tree)
            add(value);
    }
    /**
     * A constructor that builds a new AVL tree containing all unique values in the input array.
     * @param data - the values to add to tree.
     */
    public AvlTree(int[] data) {
        for(int value:data)
            add(value);
    }

    /**
     * rotates node with its right child - in order to maintain balance in the tree.
     * @param node - the node that needs to be rotated
     */
    private void rotateLeft(AVLNode node) {
        AVLNode right = node.right;
        AVLNode parent = node.parent;
        node.right = right.left;
        if (node.right != null)
            node.right.parent = node;
        right.left = node;
        right.parent = parent;
        node.parent = right;
        updateParent(right);
        }

    /**
     * doubley rotates node. - in order to maintain balance in the tree.
     * @param node - the node that needs to be rotated
     */
    private void rotateRightLeft (AVLNode node) {
        rotateRight(node.right);
        rotateLeft(node);
    }

    /**
     * doubley rotates node. - in order to maintain balance in the tree.
     * @param node - the node that needs to be rotated
     */
    private void rotateLeftRight (AVLNode node) {
        rotateLeft(node.left);
        rotateRight(node);
    }

    /**
     * rotates node with its left child - in order to maintain balance in  the tree.
     * @param node - the node that needs to be rotated
     */
    private void rotateRight(AVLNode node) {
        AVLNode left = node.left;
        AVLNode parent = node.parent;
        node.left = left.right;
        if (node.left != null)
            node.left.parent = node;
        left.right = node;
        left.parent  = parent;
        node.parent = left;
        updateParent(left);
        }

    /**
     * checks a nodes balance (this is the indicator for rotations).
     * @param node - balance of current node.
     */
    private int balance(AVLNode node) {
        int rightHeight, leftHeight;
        if (node == null)
            return -1;
        if (node.left == null)
            leftHeight = -1;
        else
        leftHeight = node.left.height;
        if (node.right == null)
            rightHeight = -1;
        else
        rightHeight = node.right.height;
        node.balance = rightHeight - leftHeight;
        return node.balance;
    }

    /**
     * Updates the parent for the node that we made changes to (rotations after insertion/deletion).
     * @param node - the node for which we'd like to update the parent
     */
    private void updateParent (AVLNode node) {
        AVLNode parent = node.parent;
        if (parent != null) {
            if (node.value < parent.value)
                parent.left = node;
            else
                parent.right = node;
        } else
            root = node;
    }

    /**
     * Add a new node with the given key to the tree.
     * @param newValue - the value of the new node to add.
     * @return true if the value to add is not already in the tree and it was successfully added, false otherwise.
     */
    public boolean add(int newValue) {
        if (contains(newValue) != -1)
            return false;
        else {
            insert(root, newValue, null);
            amountOfNodes++;
            return true;
        }
    }

    /**
     * Insertion helper.
     * @param node - the node to compare the incoming data with.
     * @param toInsert - the data we'd like to insert
     */
    private void insert (AVLNode node, int toInsert, AVLNode parent ) {
        if (node == null) {
            node = new AVLNode(toInsert);
            node.parent  = parent;
        }
        else if (toInsert < node.value) {
            insert(node.left,toInsert, node);
            if (Math.abs(balance(node)) >= RIGHTUNBALANCED)
                if (toInsert < node.left.value)
                    rotateRight(node);
                else
                    rotateLeftRight(node);
        } else if (toInsert > node.value) {
            insert(node.right,toInsert, node);
            if (Math.abs(balance(node)) >= RIGHTUNBALANCED)
                if (toInsert > node.right.value)
                    rotateLeft(node);
                else
                    rotateRightLeft (node);
        } else
            return;
        updateParent(node);
        balance(node);
        updateHeight(node);
    }
    /**
     * Updates height for node.
     * @param node   - the node for which we'd like to update it's height.
     */
    private void updateHeight (AVLNode node) {
        int rightHeight, leftHeight;
        if (node!=null) {
            if (node.left == null)
                leftHeight = -1;
            else
                leftHeight = node.left.height;
            if (node.right == null)
                rightHeight = -1;
            else
                rightHeight = node.right.height;
            node.height = Math.max(rightHeight, leftHeight) +1;
        }
    }

    /**
     *Check whether the tree contains the given input value.
     * @param searchVal   - value to search for
     * @return if val is found in the tree, return the depth of the node (0 for the root) with the given value
     * if it was found in the tree, -1 otherwise.
     */
    public int contains(int searchVal) {
        int depth = 0;
        AVLNode current = root;
        while (current != null) {
            if (current.value > searchVal)
                current = current.left;
            else if (current.value < searchVal)
                current = current.right;
            else {
                return depth;
            }
            depth ++;
        }return UNFOUND;
    }

    /**
     *delete helper function
     * @param node - the node we'd like to remove (and switch in needed)
     * @return the removed node
     */
    private AVLNode remove (AVLNode node) {
        AVLNode successor = findSuccessor(node);
        if (node.left == null && node.right == null) {
            if (node.parent != null) {
                if (node.parent.left == node)
                    node.parent.left = null;
                else
                    node.parent.right = null;
            }
        } else if (node.left == null) {
            if (node.parent.left == node)
                node.parent.left = node.right;
            else
                node.parent.right = node.right;
        } else if (node.right == null) {
            if (node.parent.left == node)
                node.parent.left = node.left;
            else
                node.parent.right = node.left;
        } else {
            node.value = successor.value;
            remove(successor);
            node = successor;
        }
        return node;
    }

    /**
     * Balances the tree after a node was deleted.
     * @param node - the node that needs to be balanced
     */
    private void deleteBalance (AVLNode node) {
        if (balance(node) == RIGHTUNBALANCED) {
            if (balance(node.right) == -1)
                rotateRight(node.right);
            rotateLeft(node);
        } if (balance(node) == LEFTUNBALANCED) {
            if (balance(node.left) == 1) {
                rotateLeft(node.left);
            }rotateRight(node);
        }updateHeight(node);
    }
    /**
     *Removes the node with the given value from the tree, if it exists.
     * @param toDelete  - the value to remove from the tree.
     * @return true if the given value was found and deleted, false otherwise.
     */
    public boolean delete (int toDelete) {
        AVLNode current = root;
        while (current != null) {
            if (current.value > toDelete)
                current = current.left;
            else if (current.value < toDelete)
                current = current.right;
            else
                break;
        }if (current == null)
            return false;
        remove(current);
        amountOfNodes--;
        while (current != null) {
            deleteBalance(current);
            current = current.parent;
        } return true;
    }

    /**
     * @return the number of nodes in the tree.
     */
    public int size() {
        return amountOfNodes;
    }

    /**
     * Acts as a helper method to our iterator method, by adding all the values of the AVL tree
     * to a linked list of integers in order.
     * @param node - node we want to add to our list of avl value.
     */
    private void iteratorHelper(AVLNode node) {
        avlList = new LinkedList<Integer>();
        if (node != null) {
            iteratorHelper(node.left);
            avlList.add(node.value);
            iteratorHelper(node.right);
        }
    }

    /**
     * @return an iterator for the Avl Tree. The returned iterator iterates over the tree nodes in an ascending order,
     * and does NOT implement the remove() method.
     */
    public java.util.Iterator<Integer> iterator() {
        iteratorHelper(root);
        return avlList.iterator();
    }

    /**
     * Finds minimum leaf on the AVL tree.
     * @param node - starts at the root until it reaches the minimum child.
     * @return the smallest
     */
    private AVLNode findMinimum (AVLNode node) {
        if (node.left == null)
            return node;
        return findMinimum(node.left);
    }
    /**
     * Finds the given node's successor.
     * @param node - the node that we'd like to find it's successor
     * @return the successor for the given node.
     */
    private AVLNode findSuccessor (AVLNode node) {
        if (node.right != null) {
            return findMinimum(node.right);
        } else {;
            while (node.parent != null && node.parent.right == node)
                node = node.parent;
            return node.parent;
        }
}

    /**
     * Calculates the minimum number of nodes in an AVL tree of height h.
     * @param h - the height of the tree (a non-negative number) in question.
     * @return the minimum number of nodes in an AVL tree of the given height.
     */
    public static int findMinNodes(int h) {
        return (int)(Math.round(((HEIGHTSQRT+2)/
                HEIGHTSQRT)*Math.pow((1+
                HEIGHTSQRT)/2,h)-1));
    }
}
