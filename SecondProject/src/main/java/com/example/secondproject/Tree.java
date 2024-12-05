package com.example.secondproject;

public class Tree<T extends Comparable<T>> {

    private TNode<T> root;

    public Tree() {
        root = null;
    }

    public Tree(T data) {
        root = new TNode<>(data);
    }

    public TNode<T> insert(T data) {
        if (isEmpty()) {
            root = new TNode<>(data);
            return root;
        }
        return insert(data, root);
    }

    private TNode<T> insert(T data, TNode<T> node) {
        int comp = data.compareTo(node.getData());
        if (comp == 0) {
            // Node with the same data already exists in the tree
            return node;
        } else if (comp < 0) {
            // Insert into the left subtree
            if (!node.hasLeft()) {
                node.setLeft(new TNode<>(data));
                return node.getLeft();
            } else {
                return insert(data, node.getLeft());
            }
        } else {
            // Insert into the right subtree
            if (!node.hasRight()) {
                node.setRight(new TNode<>(data));
                return node.getRight();
            } else {
                return insert(data, node.getRight());
            }
        }
    }

    public TNode<T> delete(T data) {
        TNode<T> current = root;
        TNode<T> parent = root;
        boolean isLeftChild = false;

        if (isEmpty()) return null; // tree is empty

        while (current != null && !current.getData().equals(data)) {
            parent = current;
            if (data.compareTo(current.getData()) < 0) {
                current = current.getLeft();
                isLeftChild = true;
            } else {
                current = current.getRight();
                isLeftChild = false;
            }
        }
        if (current == null) return null; // not found

        // Case 1: Node is a leaf
        if (!current.hasLeft() && !current.hasRight()) {
            if (current == root)
                root = null;
            else {
                if (isLeftChild)
                    parent.setLeft(null);
                else
                    parent.setRight(null);
            }
        } else if (current.hasLeft()) { // Node has left child only
            if (current == root) {
                root = current.getLeft();
            } else if (isLeftChild) {
                parent.setLeft(current.getLeft());
            } else {
                parent.setRight(current.getLeft());
            }
        } else if (current.hasRight()) { // Node has right child only
            if (current == root) {
                root = current.getRight();
            } else if (isLeftChild) {
                parent.setLeft(current.getRight());
            } else {
                parent.setRight(current.getRight());
            }
        } else { // Case 3: Node to be deleted has 2 children
            TNode<T> successor = getSuccessor(current);
            if (current == root)
                root = successor;
            else if (isLeftChild) {
                parent.setLeft(successor);
            } else {
                parent.setRight(successor);
            }
            successor.setLeft(current.getLeft());
        }

        return current;
    }

    private TNode<T> getSuccessor(TNode<T> node) {
        TNode<T> parentOfSuccessor = node;
        TNode<T> successor = node;
        TNode<T> current = node.getRight();

        while (current != null) {
            parentOfSuccessor = successor;
            successor = current;
            current = current.getLeft();
        }

        if (successor != node.getRight()) { // Fix successor connections
            parentOfSuccessor.setLeft(successor.getRight());
            successor.setRight(node.getRight());
        }

        return successor;
    }

    public TNode<T> find(T data) {
        return find(data, root);
    }

    private TNode<T> find(T data, TNode<T> node) {
        if (node != null) {
            int comp = node.getData().compareTo(data);
            if (comp == 0)
                return node;
            else if (comp > 0 && node.hasLeft())
                return find(data, node.getLeft());
            else if (comp < 0 && node.hasRight())
                return find(data, node.getRight());
        }
        return null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public TNode<T> largest() {
        return largest(root);
    }

    private TNode<T> largest(TNode<T> node) {
        if (node != null) {
            if (!node.hasRight())
                return node;
            return largest(node.getRight());
        }
        return null;
    }

    public TNode<T> smallest() {
        return smallest(root);
    }

    private TNode<T> smallest(TNode<T> node) {
        if (node != null) {
            if (!node.hasLeft())
                return node;
            return smallest(node.getLeft());
        }
        return null;
    }

    public int height() {
        return height(root);
    }

    private int height(TNode<T> node) {
        if (node == null)
            return 0;
        if (node.isLeaf())
            return 1;

        int left = 0;
        int right = 0;

        if (node.hasLeft())
            left = height(node.getLeft());

        if (node.hasRight())
            right = height(node.getRight());

        return Math.max(left, right) + 1;
    }

    public void traverseInOrder() {
        traverseInOrder(root);
    }

    private void traverseInOrder(TNode<T> node) {
        if (node != null) {
            traverseInOrder(node.getLeft());
            System.out.print(node.getData() + " ");
            traverseInOrder(node.getRight());
        }
    }


//    public void levelOrderTraversal() {
//        if (root == null) {
//            System.out.println("Tree is empty.");
//            return;
//        }
//
//        Queue <TNode<T>> queue = new SLinkedList<>(); // Use your custom SLinkedList as a queue
//        queue.enqueue(root); // Add the root node to the queue
//
//        while (!queue.isEmpty()) {
//            int levelSize = queue.size(); // Get the number of nodes in the current level
//
//            for (int i = 0; i < levelSize; i++) {
//                TNode<T> currentNode = queue.dequeue();
//                System.out.print(currentNode.data + " ");
//
//                // Enqueue the left child if it exists
//                if (currentNode.left != null) {
//                    queue.enqueue(currentNode.left);
//                }
//
//                // Enqueue the right child if it exists
//                if (currentNode.right != null) {
//                    queue.enqueue(currentNode.right);
//                }
//            }
//
//            System.out.println(); // Move to the next line after printing each level
//        }
//    }

    public void traversePreOrder() {
        traversePreOrder(root);
    }

    private void traversePreOrder(TNode<T> node) {
        if (node != null) {
            System.out.print(node.getData() + " ");
            traversePreOrder(node.getLeft());
            traversePreOrder(node.getRight());
        }
    }

    public int size() {
        return size(root);
    }

    private int size(TNode<T> node) {
        if (node == null)
            return 0;
        return 1 + size(node.getLeft()) + size(node.getRight());
    }

    public void traversePostOrder() {
        traversePostOrder(root);
    }

    private void traversePostOrder(TNode<T> node) {
        if (node != null) {
            traversePostOrder(node.getLeft());
            traversePostOrder(node.getRight());
            System.out.print(node.getData() + " ");
        }
    }
    public TNode<T> successor(TNode<T> node) {
        if (node == null) {
            return null;
        }

        // If the node has a right subtree, find the leftmost node in that subtree
        if (node.right != null) {
            return minimum(node.right);
        }

        // Otherwise, backtrack to find the first ancestor whose left child is an ancestor of 'node'
        TNode<T> successor = null;
        TNode<T> current = root;
        while (current != null) {
            if (node.data.compareTo(current.data) < 0) {
                successor = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return successor;
    }

    private TNode<T> minimum(TNode<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    public int countLeaves() {
        return countLeaves(root);
    }

    private int countLeaves(TNode<T> node) {
        if (node == null)
            return 0;
        if (node.isLeaf())
            return 1;
        return countLeaves(node.getLeft()) + countLeaves(node.getRight());
    }

    public int countParents() {
        return countParents(root);
    }

    private int countParents(TNode<T> node) {
        if (node == null || node.isLeaf())
            return 0;
        return 1 + countParents(node.getLeft()) + countParents(node.getRight());
    }

    public void clear() {
        root = null;
    }

    public TNode<T> getRoot() {
        return root;
    }

    public void setRoot(TNode<T> root) {
        this.root = root;
    }


}
