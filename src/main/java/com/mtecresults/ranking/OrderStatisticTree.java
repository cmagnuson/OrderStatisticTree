package com.mtecresults.ranking;

import java.util.*;

/**
 * This class implements an order statistic tree which is based on AVL-trees.
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Feb 11, 2016)
 * @param <T> the actual element type.
 */
public class OrderStatisticTree<T extends Comparable<? super T>>
        implements OrderStatisticSet<T> {

    private Node<T> root;
    private int size;

    @Override
    public boolean add(T element) {
        Objects.requireNonNull(element, "The input element is null.");

        if (root == null) {
            root = new Node<>(element);
            size = 1;
            return true;
        }

        Node<T> parent = null;
        Node<T> node = root;
        int cmp;

        while (node != null) {
            cmp = -1 * node.compareTo(element);

            if (cmp == 0) {
                if(node.contains(element)) {
                    // The element is already in this tree.
                    return false;
                }
                else{
                    // The element needs to be added to this node
                    node.add(element);
                    size++;
                    incrementChildren(node.parent, node);
                    return true;
                }
            }

            parent = node;

            if (cmp < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        Node<T> newnode = new Node<>(element);

        if (-1 * parent.compareTo(element) < 0) {
            parent.left = newnode;
        } else {
            parent.right = newnode;
        }

        newnode.parent = parent;
        size++;

        incrementChildren(parent, newnode);

        fixAfterModification(newnode, true);
        return true;
    }

    private void incrementChildren(Node<T> hi, Node<T> lo){
        while (hi != null) {
            if (hi.left == lo) {
                hi.count++;
            }

            lo = hi;
            hi = hi.parent;
        }
    }

    private void decrementChildren(Node<T> hi, Node<T> lo){
        while (hi != null) {
            if (hi.left == lo) {
                hi.count--;
            }

            lo = hi;
            hi = hi.parent;
        }
    }

    @Override
    public boolean remove(T element) {
        Node<T> x = root;
        int cmp;

        while (x != null && (cmp = -1 * x.compareTo(element)) != 0) {
            if (cmp < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        if (x == null || !x.contains(element)) {
            return false;
        }

        if(x.size() > 1){
            //remove this entry from node, leaving it non-empty
            x.remove(element);
            decrementSize();
            decrementChildren(x.parent, x);
            return true;
        }

        x = deleteNode(x);
        fixAfterModification(x, false);
        decrementSize();
        return true;
    }

    @Override
    //get based on place - 1 based
    //may return empty set if there is a tie so intermediate place does not exist
    public Set<T> get(int index) {
        //adjust index supplied to be 0 based internally
        index--;

        checkIndex(index);
        Node<T> node = root;

        while (true) {
            if(node == null){
                return Collections.EMPTY_SET;
            }
            if (index > node.count) {
                index -= node.count + node.size();
                node = node.right;
            } else if (index < node.count) {
                node = node.left;
            } else {
                return node.getKeys();
            }
        }
    }

    //kept in to keep older test cases working
    //indexOf 0 based behavior
    protected int indexOf(T element){
        int index = rankOf(element);
        if(index > 0){
            index--;
        }
        return index;
    }

    //1 based rank of element in tree
    //return -1 if not found
    public int rankOf(T element) {
        Node<T> node = root;

        if (root == null) {
            return -1;
        }

        int rank = root.count;
        int cmp;

        while (true) {
            cmp = -1 * node.compareTo(element);
            if (cmp < 0) {
                if (node.left == null) {
                    return -1;
                }

                rank -= (node.count - node.left.count);
                node = node.left;
            } else if (cmp > 0) {
                if (node.right == null) {
                    return -1;
                }

                rank += node.size() + node.right.count;
                node = node.right;
            } else {
                //adjust for ties
                //rank -= node.size() - 1;
                break;
            }
        }

        if(!node.contains(element)){
            return -1;
        }
        //adjust to be 1 based
        rank++;
        return rank;
    }

    @Override
    public int size() {
        return size;
    }

    private void decrementSize() {
        if (size > 0) {
            size--;
        }
    }


    //minimum value is 0, max is size-1
    //note that values in between may not have entries because of ties
    //which live at one index, but "ghost" multiple other indexes
    private void checkIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(
                    "The input index is negative: " + index);
        }

        if (index >= size) {
            throw new IndexOutOfBoundsException(
                    "The input index is too large: " + index +
                            ", the size of this tree is " + size);
        }
    }

    private Node<T> deleteNode(Node<T> node) {
        if (node.left == null && node.right == null) {
            // 'node' has no children.
            Node<T> parent = node.parent;

            if (parent == null) {
                // 'node' is the root node of this tree.
                root = null;
                size = 0;
                return node;
            }

            decrementChildren(parent, node);

            if (node == parent.left) {
                parent.left = null;
            } else {
                parent.right = null;
            }

            return node;
        }

        if (node.left == null || node.right == null) {
            Node<T> child;

            // 'node' has only one child.
            if (node.left != null) {
                child = node.left;
            } else {
                child = node.right;
            }

            Node<T> parent = node.parent;
            child.parent = parent;

            if (parent == null) {
                root = child;
                return node;
            }

            if (node == parent.left) {
                parent.left = child;
            } else {
                parent.right = child;
            }

            decrementChildren(parent, child);

            return node;
        }

        // 'node' has both children.
        Set<T> tmpKey = node.getKeys();
        Node<T> successor = minimumNode(node.right);
        node.setKeys(successor.getKeys());
        Node<T> child = successor.right;
        Node<T> parent = successor.parent;

        if (parent.left == successor) {
            parent.left = child;
        } else {
            parent.right = child;
        }

        if (child != null) {
            child.parent = parent;
        }

        decrementChildren(parent, child);
        successor.setKeys(tmpKey);
        return successor;
    }

    private Node<T> minimumNode(Node<T> node) {
        while (node.left != null) {
            node = node.left;
        }

        return node;
    }

    private int height(Node<T> node) {
        return node == null ? -1 : node.height;
    }

    private Node<T> leftRotate(Node<T> node1) {
        Node<T> node2 = node1.right;
        node2.parent = node1.parent;
        node1.parent = node2;
        node1.right = node2.left;
        node2.left = node1;

        if (node1.right != null) {
            node1.right.parent = node1;
        }

        node1.height = Math.max(height(node1.left), height(node1.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        node2.count += node1.count + node1.size();
        return node2;
    }

    private Node<T> rightRotate(Node<T> node1) {
        Node<T> node2 = node1.left;
        node2.parent = node1.parent;
        node1.parent = node2;
        node1.left = node2.right;
        node2.right = node1;

        if (node1.left != null) {
            node1.left.parent = node1;
        }

        node1.height = Math.max(height(node1.left), height(node1.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        node1.count -= node2.count + node2.size();
        return node2;
    }

    private Node<T> rightLeftRotate(Node<T> node1) {
        Node<T> node2 = node1.right;
        node1.right = rightRotate(node2);
        return leftRotate(node1);
    }

    private Node<T> leftRightRotate(Node<T> node1) {
        Node<T> node2 = node1.left;
        node1.left = leftRotate(node2);
        return rightRotate(node1);
    }

    // Fixing an insertion: use insertionMode = true.
    // Fixing a deletion: use insertionMode = false.
    private void fixAfterModification(Node<T> node, boolean insertionMode) {
        Node<T> parent = node.parent;
        Node<T> grandParent;
        Node<T> subTree;

        while (parent != null) {
            if (height(parent.left) == height(parent.right) + 2) {
                grandParent = parent.parent;

                if (height(parent.left.left) >= height(parent.left.right)) {
                    subTree = rightRotate(parent);
                } else {
                    subTree = leftRightRotate(parent);
                }

                if (grandParent == null) {
                    root = subTree;
                } else if (grandParent.left == parent) {
                    grandParent.left = subTree;
                } else {
                    grandParent.right = subTree;
                }

                if (grandParent != null) {
                    grandParent.height = Math.max(
                            height(grandParent.left),
                            height(grandParent.right)) + 1;
                }

                if (insertionMode) {
                    // Whenever fixing after insertion, at most one rotation is
                    // required in order to maintain the balance.
                    return;
                }
            } else if (height(parent.right) == height(parent.left) + 2) {
                grandParent = parent.parent;

                if (height(parent.right.right) >= height(parent.right.left)) {
                    subTree = leftRotate(parent);
                } else {
                    subTree = rightLeftRotate(parent);
                }

                if (grandParent == null) {
                    root = subTree;
                } else if (grandParent.left == parent) {
                    grandParent.left = subTree;
                } else {
                    grandParent.right = subTree;
                }

                if (grandParent != null) {
                    grandParent.height =
                            Math.max(height(grandParent.left),
                                    height(grandParent.right)) + 1;
                }

                if (insertionMode) {
                    return;
                }
            }

            parent.height = Math.max(height(parent.left),
                    height(parent.right)) + 1;
            parent = parent.parent;
        }
    }

    public boolean isHealthy() {
        if (root == null) {
            return true;
        }

        return !containsCycles()
                && heightsAreCorrect()
                && isBalanced()
                && isWellIndexed();
    }

    protected boolean containsCycles() {
        Set<Node<T>> visitedNodes = new HashSet<>();
        return containsCycles(root, visitedNodes);
    }

    private boolean containsCycles(Node<T> current, Set<Node<T>> visitedNodes) {
        if (current == null) {
            return false;
        }

        if (visitedNodes.contains(current)) {
            return true;
        }

        visitedNodes.add(current);

        return containsCycles(current.left, visitedNodes)
                || containsCycles(current.right, visitedNodes);
    }

    protected boolean heightsAreCorrect() {
        return getHeight(root) == root.height;
    }

    private int getHeight(Node<T> node) {
        if (node == null) {
            return -1;
        }

        int leftTreeHeight = getHeight(node.left);

        if (leftTreeHeight == Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }

        int rightTreeHeight = getHeight(node.right);

        if (rightTreeHeight == Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }

        if (node.height == Math.max(leftTreeHeight, rightTreeHeight) + 1) {
            return node.height;
        }

        return Integer.MIN_VALUE;
    }

    protected boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isBalanced(Node<T> node) {
        if (node == null) {
            return true;
        }

        if (!isBalanced(node.left)) {
            return false;
        }

        if (!isBalanced(node.right)) {
            return false;
        }

        int leftHeight  = height(node.left);
        int rightHeight = height(node.right);

        return Math.abs(leftHeight - rightHeight) < 2;
    }

    protected boolean isWellIndexed() {
        return size == count(root);
    }

    private int count(Node<T> node) {
        if (node == null) {
            return 0;
        }

        int leftTreeSize = count(node.left);

        if (node.count != leftTreeSize) {
            throw new RuntimeException("Node count not equal to left tree size: "+node.count+" "+leftTreeSize);
        }

        int rightTreeSize = count(node.right);

        return leftTreeSize + node.size() + rightTreeSize;
    }

    public boolean isEmpty() {
        return size == 0;
    }
    public void clear() {
        root = null;
        size = 0;
    }
    public boolean contains(T element){
        return indexOf(element) > -1;
    }
    public boolean containsAll(Collection<T> elements){
        for(T element: elements){
            if(!contains(element)){
                return false;
            }
        }
        return true;
    }
    public boolean removeAll(Collection<T> elements){
        boolean modified = false;
        for(T element: elements){
            if(remove(element)){
                modified = true;
            }
        }
        return modified;
    }
    public boolean addAll(Collection<T> elements){
        boolean modified = false;
        for(T element: elements){
            if(add(element)){
                modified  = true;
            }
        }
        return modified;
    }

}
