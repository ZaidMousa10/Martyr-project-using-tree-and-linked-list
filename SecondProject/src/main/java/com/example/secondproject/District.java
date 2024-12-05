package com.example.secondproject;


public class District implements Comparable<District>{
    private String districtName;
    private Tree<Location> location;

    public District(String districtName){
        this.districtName = districtName;
        location = new Tree();
    }

    public String getDistrictName(){
        return districtName;
    }
    public void setDistrictName(String districtName){
        this.districtName = districtName;
    }

    public Tree<Location> getLocation() {
        return location;
    }
    public void setLocation(Tree<Location> location) {
        this.location = location;
    }

    public int getTotalMartyrs() {
        return calculateTotalMartyrsInLocationTree(location.getRoot());
    }

    private int calculateTotalMartyrsInLocationTree(TNode<Location> node) {
        if (node == null) {
            return 0;
        }

        int totalMartyrs = 0;
        Location location = node.getData();

        if (location != null && location.getDate() != null) {
            totalMartyrs += calculateTotalMartyrsInDateTree(location.getDate().getRoot());
        }

        totalMartyrs += calculateTotalMartyrsInLocationTree(node.getLeft());
        totalMartyrs += calculateTotalMartyrsInLocationTree(node.getRight());

        return totalMartyrs;
    }

    private int calculateTotalMartyrsInDateTree(TNode<MDate> node) {
        if (node == null) {
            return 0;
        }

        int totalMartyrs = 0;
        MDate date = node.getData();

        if (date != null && date.getMartyr() != null) {
            totalMartyrs += date.getMartyr().length();
        }

        totalMartyrs += calculateTotalMartyrsInDateTree(node.getLeft());
        totalMartyrs += calculateTotalMartyrsInDateTree(node.getRight());

        return totalMartyrs;
    }

    @Override
    public int compareTo(District other) {
        return districtName.compareToIgnoreCase(other.districtName);
    }

    @Override
    public String toString() {
        return districtName;
    }
}
