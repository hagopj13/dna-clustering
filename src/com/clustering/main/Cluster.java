package com.clustering.main;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	
    private String clusterName;
    private Cluster parentCluster;
    private List<Cluster> children;
    private List<String> leafs;
    private double distance;    
    private int representativeIndex;

    public Cluster(String clusterName) {
        this.clusterName = clusterName;
        leafs = new ArrayList<String>();
        representativeIndex = 0;
    }
    
    public boolean isRoot() {
    	return getParentCluster() == null;
    }

    public boolean isLeaf() {
        return getChildren().size() == 0;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<Cluster> getChildren() {
        children = (children == null) ? new ArrayList<Cluster>() : children;
        return children;
    }

    public void addLeafName(String leaf) {
        leafs.add(leaf);
    }

    public void appendLeafNames(List<String> leafNames){
        leafs.addAll(leafNames);
    }

    public List<String> getLeafNames() {
        return leafs;
    }
    
    public void emptyLeafNames() {
    	leafs = new ArrayList<String>();
    }

    public void setChildren(List<Cluster> children) {
        this.children = children;
    }

    public Cluster getParentCluster() {
        return parentCluster;
    }

    public void setParentCluster(Cluster parentCluster){
        this.parentCluster = parentCluster;
    }

    public String getName() {
        return clusterName;
    }

    public void setName(String clusterName) {
        this.clusterName = clusterName;
    }
    
    public int getRepresentativeIndex() {
    	return representativeIndex;
    }
    
    public void setRepresentativeIndex(int representativeIndex) {
    	this.representativeIndex = representativeIndex;
    }

    public void addChild(Cluster cluster) {
        getChildren().add(cluster);
    }

    public String toString() {
        String toReturn = getName() + ": ";
        for(String leafName: leafs) 
        	toReturn += leafName + "  ";
        return toReturn;
    }

    public int countLeafs() {
        return getLeafNames().size();
    }

}
