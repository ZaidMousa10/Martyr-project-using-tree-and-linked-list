package com.example.secondproject;



public class Location  implements Comparable <Location> {
    private String location;
    private Tree<MDate> date;

    public Location(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public Tree<MDate> getDate() {
        return date;
    }
    public void setDate(Tree<MDate> date) {
        this.date = date;
    }

    public MDate getEarliestMartyrDate() {
        return date.smallest().data;
    }

    public MDate getLatestMartyrDate() {
        return date.largest().data;
    }
    public void addMartyr(MDate martyrDate, Martyr newMartyr) {

        date.insert(martyrDate);
        martyrDate.getMartyr().insert(newMartyr);
    }
    public Queue<Martyr> findMartyrByPartOfName(String partialName) {
        Queue<Martyr> martyrSLinkedList = new Queue<>();
        if (date == null || date.getRoot() == null) {
            return martyrSLinkedList; // No dates available
        }
        SNode<Martyr> head = date.getRoot().getData().getMartyr().getHead();
        while(head!= null) {
            if (head.getData().getName().toLowerCase().contains(partialName.toLowerCase())) {
                martyrSLinkedList.enqueue(head.getData());
            }
            head = head.getNext();
        }

        // Start recursive search for the partial name within the tree
        return martyrSLinkedList;
    }

   /* private Martyr findMartyrByPartOfNameHelper(TNode<MDate> currentNode, String partialName) {
        if (currentNode == null) {
            return null;
        }

        MDate currentDate = currentNode.data;
        if (currentDate != null && currentDate.getMartyr() != null) {
            // Traverse through the linked list of martyrs for the current MDate
            Martyr foundMartyr = findMartyrInLinkedList(currentDate.getMartyr().getHead(), partialName);
            if (foundMartyr != null) {
                return foundMartyr; // Found the martyr with partial name match
            }
        }

        // Recursively search in the left and right subtrees
        Martyr foundMartyr = findMartyrByPartOfNameHelper(currentNode.left, partialName);
        if (foundMartyr != null) {
            return foundMartyr;
        }

        return findMartyrByPartOfNameHelper(currentNode.right, partialName);
    }*/

    private Martyr findMartyrInLinkedList(SNode<Martyr> head, String partialName) {
        SNode<Martyr> current = head;
        while (current != null) {
            Martyr currentMartyr = current.getData();
            if (currentMartyr != null && currentMartyr.getName().toLowerCase().contains(partialName.toLowerCase())) {
                return currentMartyr; // Found the martyr with partial name match
            }
            current = current.getNext();
        }
        return null; // Martyr not found in the linked list
    }
    public SLinkedList<Martyr> displayMartyrsSortedByName(MDate date) {
        if (date == null || date.getMartyr() == null || date.getMartyr().isEmpty()) {
            System.out.println("No martyrs available for this date.");
            return null;
        }

        SLinkedList<Martyr> martyrsList = date.getMartyr();
        SLinkedList<Martyr> sortedMartyrs = new SLinkedList<>();

        SNode<Martyr> currentMartyrNode = martyrsList.getHead();
        while (currentMartyrNode != null) {
            Martyr currentMartyr = currentMartyrNode.getData();
            sortedMartyrs.insert(currentMartyr); // Insert each martyr into the sorted list
            currentMartyrNode = currentMartyrNode.getNext();
        }

        return sortedMartyrs;
    }


    public Martyr findMartyrByFullName(String fullName) {
        // Traverse through the tree to find the specified martyr by full name
        if (date != null) {
            TNode<MDate> currentNode = date.getRoot();
            while (currentNode != null) {
                MDate currentDate = currentNode.data;
                if (currentDate != null && currentDate.getMartyr() != null) {
                    // Traverse through the linked list of martyrs for the current MDate
                    SLinkedList<Martyr> martyrsList = currentDate.getMartyr();
                    SNode<Martyr> currentMartyrNode = martyrsList.getHead();
                    while (currentMartyrNode != null) {
                        Martyr currentMartyr = currentMartyrNode.getData();
                        if (currentMartyr != null && currentMartyr.getName().equalsIgnoreCase(fullName)) {
                            return currentMartyr; // Found the martyr by full name
                        }
                        currentMartyrNode = currentMartyrNode.getNext();
                    }
                }
                // Move to the next node in the tree (inorder traversal)
                currentNode = date.successor(currentNode);
            }
        }
        return null; // Martyr not found by the specified full name
    }
    public boolean removeMartyr(Martyr martyr) {
        if (date == null || date.getRoot() == null) {
            return false; // No dates available
        }

        // Start recursive search for the martyr within the tree
        return removeMartyrHelper(date.getRoot(), martyr);
    }

    private boolean removeMartyrHelper(TNode<MDate> currentNode, Martyr martyr) {
        if (currentNode == null) {
            return false; // Martyr not found
        }

        MDate currentDate = currentNode.data;
        if (currentDate != null && currentDate.getMartyr() != null) {
            // Try to remove the martyr from the linked list of martyrs for the current MDate
            SLinkedList<Martyr> martyrsList = currentDate.getMartyr();
            if (removeMartyrFromLinkedList(martyrsList, martyr)) {
                return true; // Martyr successfully removed
            }
        }

        // Recursively search in the left and right subtrees
        if (removeMartyrHelper(currentNode.left, martyr)) {
            return true;
        }

        return removeMartyrHelper(currentNode.right, martyr);
    }

    private boolean removeMartyrFromLinkedList(SLinkedList<Martyr> martyrsList, Martyr martyr) {
        if (martyrsList == null || martyrsList.isEmpty()) {
            return false; // Empty or null linked list
        }

        SNode<Martyr> current = martyrsList.getHead();
        SNode<Martyr> previous = null;

        while (current != null) {
            Martyr currentMartyr = current.getData();
            if (currentMartyr != null && currentMartyr.equals(martyr)) {
                // Found the martyr to remove
                if (previous == null) {
                    // Removing the head of the list
                    martyrsList.setHead(current.getNext());
                } else {
                    // Removing from somewhere in the middle or end of the list
                    previous.setNext(current.getNext());
                }
                // Decrease the length of the linked list
                martyrsList.length();
                return true;
            }
            previous = current;
            current = current.getNext();
        }

        return false; // Martyr not found in the linked list
    }

    public String getSortedMartyrsInfo(MDate date) {
        if (date == null || date.getMartyr() == null) {
            return null;
        }

        String sortedMartyrsInfo = "";
        SNode<Martyr> current = date.getMartyr().getHead();

        while (current != null) {
            Martyr martyr = current.getData();
            if (martyr != null) {
                sortedMartyrsInfo += martyr.toString() + "\n";
            }
            current = current.getNext();
        }

        return sortedMartyrsInfo;
    }


    public double calculateAverageAge(MDate date) {
        if (date == null || date.getMartyr() == null || date.getMartyr().isEmpty()) {
            return 0.0; // No dates or martyrs
        }

        int totalAge = 0;
        int count = 0;

        // Traverse the tree of MDate objects (assuming depth-first traversal)
        Stack<TNode<MDate>> stack = new Stack<>();
        TNode<MDate> currentNode = this.date.getRoot();

        // Perform iterative traversal (e.g., depth-first traversal)
        while (currentNode != null || !stack.isEmpty()) {
            while (currentNode != null) {
                // Process current node (currentDate)
                MDate currentDate = currentNode.getData();

                if (currentDate != null && currentDate.getMartyr() != null) {
                    SNode<Martyr> currentMartyrNode = currentDate.getMartyr().getHead();

                    // Iterate over martyrs linked list
                    while (currentMartyrNode != null) {
                        Martyr currentMartyr = currentMartyrNode.getData();

                        if (currentMartyr != null && currentMartyr.getAge() != -1) {
                            totalAge += currentMartyr.getAge();
                            count++;
                        }

                        currentMartyrNode = currentMartyrNode.getNext();
                    }
                }

                // Push right child (if exists) onto stack
                if (currentNode.getRight() != null) {
                    stack.push(currentNode.getRight());
                }

                // Move to left child
                currentNode = currentNode.getLeft();
            }

            // Pop a node from stack to process its subtree
            if (!stack.isEmpty()) {
                currentNode = stack.pop();
            }
        }

        if (count > 0) {
            return (double) totalAge / count; // Calculate average age
        } else {
            return 0.0; // No valid ages to calculate average
        }
    }


    public Martyr findOldestMartyr(MDate date) {
        if (this.date == null || this.date.isEmpty()) {
            return null; // No dates or martyrs
        }

        Martyr oldestMartyr = null;
        int oldestAge = Integer.MIN_VALUE;

        // Traverse the tree of MDate objects
        TNode<MDate> currentNode = this.date.getRoot();
        while (currentNode != null) {
            MDate currentDate = currentNode.data;
            if (currentDate != null && currentDate.getMartyr() != null) {
                SNode<Martyr> currentMartyrNode = currentDate.getMartyr().getHead();
                while (currentMartyrNode != null) {
                    Martyr currentMartyr = currentMartyrNode.getData();
                    if (currentMartyr != null && currentMartyr.getAge() != -1 && currentMartyr.getAge() > oldestAge) {
                        oldestMartyr = currentMartyr;
                        oldestAge = currentMartyr.getAge();
                    }
                    currentMartyrNode = currentMartyrNode.getNext();
                }
            }
            currentNode = this.date.successor(currentNode);
        }

        return oldestMartyr;
    }
    public Martyr findYoungestMartyr(MDate date) {
        if (date == null || date.getMartyr() == null || date.getMartyr().isEmpty()) {
            return null; // No dates or martyrs
        }

        Martyr youngestMartyr = null;
        int youngestAge = Integer.MAX_VALUE;

        // Stack for iterative traversal
        Stack<TNode<MDate>> stack = new Stack<>();
        TNode<MDate> currentNode = this.date.getRoot();

        // Perform iterative traversal (e.g., depth-first traversal)
        while (currentNode != null || !stack.isEmpty()) {
            while (currentNode != null) {
                // Process current node (currentDate)
                MDate currentDate = currentNode.getData();

                if (currentDate != null && currentDate.getMartyr() != null) {
                    SNode<Martyr> currentMartyrNode = currentDate.getMartyr().getHead();

                    // Iterate over martyrs linked list
                    while (currentMartyrNode != null) {
                        Martyr currentMartyr = currentMartyrNode.getData();

                        if (currentMartyr != null && currentMartyr.getAge() != -1 && currentMartyr.getAge() < youngestAge) {
                            youngestMartyr = currentMartyr;
                            youngestAge = currentMartyr.getAge();
                        }

                        currentMartyrNode = currentMartyrNode.getNext();
                    }
                }

                // Push right child (if exists) onto stack
                if (currentNode.getRight() != null) {
                    stack.push(currentNode.getRight());
                }

                // Move to left child
                currentNode = currentNode.getLeft();
            }

            // Pop a node from stack to process its subtree
            if (!stack.isEmpty()) {
                currentNode = stack.pop();
            }
        }

        return youngestMartyr;
    }
    public MDate getMaxMartyrsDate() {
        if (date == null || date.getRoot() == null) {
            return null;
        }

        return getMaxMartyrsDateFromNode(date.getRoot());
    }

    private MDate getMaxMartyrsDateFromNode(TNode<MDate> node) {
        if (node == null) {
            return null;
        }

        MDate maxDate = node.getData();
        int maxCount = (maxDate != null && maxDate.getMartyr() != null) ? maxDate.getMartyr().length() : 0;

        MDate leftMaxDate = getMaxMartyrsDateFromNode(node.getLeft());
        MDate rightMaxDate = getMaxMartyrsDateFromNode(node.getRight());

        int leftCount = (leftMaxDate != null && leftMaxDate.getMartyr() != null) ? leftMaxDate.getMartyr().length() : 0;
        int rightCount = (rightMaxDate != null && rightMaxDate.getMartyr() != null) ? rightMaxDate.getMartyr().length() : 0;

        if (leftCount > maxCount) {
            maxDate = leftMaxDate;
            maxCount = leftCount;
        }

        if (rightCount > maxCount) {
            maxDate = rightMaxDate;
        }

        return maxDate;
    }

    private MDate findMartyrDateHelper(TNode<MDate> currentNode, String date) {
        if (currentNode == null) {
            return null;
        }

        MDate currentMDate = currentNode.data;
        if (currentMDate != null && currentMDate.getDate().equals(date)) {
            return currentMDate; // Found the matching date
        }

        MDate leftSearch = findMartyrDateHelper(currentNode.left, date);
        if (leftSearch != null) {
            return leftSearch;
        }

        return findMartyrDateHelper(currentNode.right, date);
    }



    @Override
    public int compareTo(Location o) {
        return this.location.compareToIgnoreCase(o.location);
    }

    @Override
    public String toString() {
        return  location ;
    }


}




