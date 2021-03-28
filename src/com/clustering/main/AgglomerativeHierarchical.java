package com.clustering.main;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AgglomerativeHierarchical {
	
	static double[][] originalMatrix;
	static int numberOfCluster = 0;
	static Cluster rootCluster;
	
	public static Cluster performHierarchicalClustering(ArrayList<String> sequences, int evaluationMethod, double threshold, int k) throws Exception {
		if (sequences.isEmpty())
			throw new Exception ("No data entered");
		rootCluster = new Cluster ("root");
		ArrayList<Cluster> remainingClustersList = new ArrayList<Cluster> ();
		numberOfCluster = 0;
		for (String sequence: sequences){
			Cluster child = new Cluster(sequence);
			child.addLeafName(sequence);
			child.setParentCluster(rootCluster);
			remainingClustersList.add(child);
		}
		
		originalMatrix = SequenceEdit.getOriginalMatrix(sequences);
		System.out.println("Original Matrix");
		printMatrix(originalMatrix);
		
		while (remainingClustersList.size() > 2) {
			double [][] tempMatrix = getTempMatrix(remainingClustersList, sequences, evaluationMethod);
			System.out.println("Temp Matrix at iteration: " + (sequences.size() - remainingClustersList.size() + 1));
			printMatrix(tempMatrix);
			
			//find minimum in tempMatrix
			int minI = 0;
			int minJ = 0;
			double minTempDistance = Integer.MAX_VALUE;
			for (int j=0; j<tempMatrix.length; j++) {
				for (int i=j; i<tempMatrix.length; i++)
					if (i != j && tempMatrix[i][j]<minTempDistance) {
						minTempDistance = tempMatrix[i][j];
						minI = i;
						minJ = j;
					}
			}
			
			if (threshold > 0) 
				if (minTempDistance > threshold)
					break;
			
			formNewCluster(remainingClustersList, minI, minJ, minTempDistance, sequences);	
			
			if (k > 0)
				if (remainingClustersList.size() == k)
					break;
		}
		
		//add whatever is left in the arraylist to the root
		if (remainingClustersList.size() == 2) {
			double [][] tempMatrix = getTempMatrix(remainingClustersList, sequences, evaluationMethod);
			printMatrix(tempMatrix);
			int minI = 1;
			int minJ = 0;
			double minTempDistance = tempMatrix[minI][minJ];
			if ((threshold == 0.0 && k == 0) || (threshold > 0.0 && minTempDistance <= threshold)) {
				formNewCluster(remainingClustersList, minI, minJ, minTempDistance, sequences);
				Cluster lastCluster = remainingClustersList.get(0);
				rootCluster.appendLeafNames(lastCluster.getLeafNames());
				rootCluster.setDistance(1 + lastCluster.getDistance());
				rootCluster.setChildren(remainingClustersList);
			}
		}
		
		if (remainingClustersList.size() > 1) {
			System.out.println("Stop hierarchical clustering");
			double sumOfDistances = 0;
			int count = 0;
			for (int i=0; i<remainingClustersList.size(); i++) 
				for (int j=i; j<remainingClustersList.size(); j++) 
					if (i != j) {
						Cluster firstCluster = remainingClustersList.get(i);
						Cluster secondCluster = remainingClustersList.get(j);
						sumOfDistances += getLinkDistance(firstCluster, secondCluster, evaluationMethod, sequences);
						count++;
					}
			double averageDistance = sumOfDistances / count;
			Cluster lastCluster = new Cluster("lastCluster");
			lastCluster.setDistance(averageDistance);
			lastCluster.setChildren(remainingClustersList);
			lastCluster.setParentCluster(rootCluster);
			for (Cluster cluster: remainingClustersList)
				lastCluster.appendLeafNames(cluster.getLeafNames());
			
			rootCluster.appendLeafNames(lastCluster.getLeafNames());
			rootCluster.setDistance(1 + lastCluster.getDistance());
			rootCluster.addChild(lastCluster);
			System.out.println("Remaining cluster: ");
			for(Cluster cluster: lastCluster.getChildren()) {
				System.out.print(cluster.getName() + "  ");
			}
		}
		
		return rootCluster;
	}

	private static double[][] getTempMatrix (ArrayList<Cluster> remainingClustersList, ArrayList<String> sequences, int evaluationMethod) throws Exception {
		double [][] tempMatrix = new double [remainingClustersList.size()][remainingClustersList.size()];
		for (int j=0; j<tempMatrix.length; j++) {
			for (int i=j; i<tempMatrix.length; i++) {
				if (i == j)
					tempMatrix[i][j] = 0;
				else {
					double linkDistance = 0;
					Cluster firstCluster = remainingClustersList.get(i);
					Cluster secondCluster = remainingClustersList.get(j);
					linkDistance = getLinkDistance(firstCluster, secondCluster, evaluationMethod, sequences);
					tempMatrix[i][j] = linkDistance;
					tempMatrix[j][i] = linkDistance;
				}
			}
		}
		return tempMatrix;
	}
	
	private static void formNewCluster (ArrayList<Cluster> remainingClustersList, int minI, int minJ, double minTempDistance, ArrayList<String> sequences) {
		Cluster firstChild = remainingClustersList.get(minI);
		Cluster secondChild = remainingClustersList.get(minJ);
		remainingClustersList.remove(minI);
		remainingClustersList.remove(minJ);
		
		Cluster newCluster = new Cluster ("Cluster" + ++numberOfCluster);
		firstChild.setParentCluster(newCluster);
		secondChild.setParentCluster(newCluster);
		newCluster.setParentCluster(rootCluster);
		newCluster.setDistance(minTempDistance);
		newCluster.appendLeafNames(firstChild.getLeafNames());
		newCluster.appendLeafNames(secondChild.getLeafNames());
		newCluster.addChild(firstChild);
		newCluster.addChild(secondChild);
		
		//find representative sequence
		double minAverageDistance = Double.MAX_VALUE;
		int representativeIndex = 0;
		ArrayList<String> leafNames = (ArrayList<String>) newCluster.getLeafNames();
		for (int i=0; i<leafNames.size(); i++) {
			double currentSum = 0;
			for (int j=0; j<leafNames.size(); j++) {
				if (i != j) 
					currentSum += originalMatrix[sequences.indexOf(leafNames.get(i))][sequences.indexOf(leafNames.get(j))];
			}
			double currentAverage = currentSum / (leafNames.size()-1);
			if (currentAverage < minAverageDistance) {
				minAverageDistance = currentAverage;
				representativeIndex = i;
			}
		}
		newCluster.setRepresentativeIndex(representativeIndex);
		
		DecimalFormat df = new DecimalFormat("0.0#");
		System.out.println("Cluster name: " + newCluster.getName());
		System.out.println("Merge " + firstChild.getName() + " with " + secondChild.getName());
		System.out.println("Cluster Distance is: " + df.format(newCluster.getDistance()));
		System.out.println("Cluster representative is: " + leafNames.get(representativeIndex)+ " at index " + representativeIndex);
		System.out.println();
		System.out.println("--------------------------");
		System.out.println();
		remainingClustersList.add(newCluster);
	}

	private static void printMatrix (double [][] matrix) {
		DecimalFormat df = new DecimalFormat("0.0#");
		for (int i=0; i<matrix.length; i++) {
			for (int j=0; j<matrix.length; j++)
				System.out.print(df.format(matrix[i][j]) + " ");
			System.out.println();
		}
		System.out.println();
	}
	
	private static double getLinkDistance (Cluster firstCluster, Cluster secondCluster, int evaluationMethod, ArrayList<String> sequences) throws Exception {
		double linkDistance = 0;
		ArrayList<String> firstSet = (ArrayList<String>)firstCluster.getLeafNames();
		ArrayList<String> secondSet = (ArrayList<String>)secondCluster.getLeafNames();
		
		switch(evaluationMethod) {
			case 1:
				double min = Integer.MAX_VALUE;
				for (String setObject1: firstSet) {
					for (String setObject2: secondSet) {
						double setObjectDistance = originalMatrix[sequences.indexOf(setObject1)][sequences.indexOf(setObject2)];
						if (setObjectDistance < min)
							min = setObjectDistance;
					}
				}
				linkDistance = min;
				break;
			case 2:
				double max = Integer.MIN_VALUE;
				for (String setObject1: firstSet) {
					for (String setObject2: secondSet) {
						double setObjectDistance = originalMatrix[sequences.indexOf(setObject1)][sequences.indexOf(setObject2)];
						if (setObjectDistance > max)
							max = setObjectDistance;
					}
				}
				linkDistance = max;
				break;
			case 3:
				int count = 0;
				double sum = 0.0;
				for (String setObject1: firstSet) {
					for (String setObject2: secondSet) {
						double setObjectDistance = originalMatrix[sequences.indexOf(setObject1)][sequences.indexOf(setObject2)];
						sum += setObjectDistance;
						count++;
					}
				}
				linkDistance = sum / count;
				break;
			case 4:
				String firstRepresentative = firstSet.get(firstCluster.getRepresentativeIndex());
				String secondRepresentative = secondSet.get(secondCluster.getRepresentativeIndex());
				linkDistance = originalMatrix [sequences.indexOf(firstRepresentative)][sequences.indexOf(secondRepresentative)];
				break;
			default:
				throw new Exception ("Invalid Evaluation Method");
		}
		
		return linkDistance;
	}
}
