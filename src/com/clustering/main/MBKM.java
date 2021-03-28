package com.clustering.main;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.plaf.synth.SynthSeparatorUI;

public class MBKM {
	
	private static ArrayList<Cluster> clusters;
	private static double [][] originalMatrix;
	private static ArrayList <String> leafNames;
	private static ArrayList <String> storeLeafNames;
	private static int iterationNumber = 0;
	private static ArrayList<Cluster> tempClusters;
	private static int clusterNumber = 0;
	private static ArrayList<String> previousCenters;
	private static int splitNumber = 0;
	
	public static ArrayList<Cluster> performMBKMClustering(ArrayList<String> sequences, int k) throws Exception {
		if (sequences.size() < k)
			throw new Exception ("The number of sequences is less than k");
		clusterNumber = 0;
		iterationNumber = 0;
		splitNumber = 0;
		clusters = new ArrayList<Cluster> ();
		originalMatrix = SequenceEdit.getOriginalMatrix(sequences);
		
		Cluster initialCluster = new Cluster("initial");
		initialCluster.appendLeafNames(sequences);
		clusters.add(initialCluster);
		
		if (k > 1) {
			System.out.println("SPLIT CLUSTER: " + initialCluster);
			splitCluster(sequences, initialCluster);
			clusters.remove(0);
		}
		
		System.out.println("After performing split#" + ++splitNumber + " the clusters are the following");
		for(Cluster c: clusters) {
			System.out.println(c);
		}
		while(clusters.size() < k) {
			//find the cluster with the largest variance
			DecimalFormat df = new DecimalFormat("0.00");
			System.out.println("\nFind variance of the current clusters");
			double maxVariance = Integer.MIN_VALUE;
			int maxIndex = 0;
			for (int i=0; i<clusters.size(); i++) {
				Cluster cluster = clusters.get(i);
				double var = calculateVariance(cluster, sequences);
				System.out.println("Variance of " + cluster.getName() + ": " + df.format(var));
				if (var > maxVariance) {
					maxIndex = i;
					maxVariance = var;
				}
			}
			System.out.println("\nCluster with maximum variance is " + clusters.get(maxIndex).getName() + "\n");
			System.out.println("\n************************************************************\n");
			System.out.println("Split " + clusters.get(maxIndex));
			splitCluster(sequences, clusters.get(maxIndex));
			clusters.remove(maxIndex);
			
			System.out.println("After performing split#" + ++splitNumber + " the clusters are the following");
			for(Cluster c: clusters) {
				System.out.println(c);
			}
		}
		System.out.println("\nDone with mBKM !!!");
		return clusters;
	} 
	
	private static void splitCluster (ArrayList<String> sequences, Cluster cluster) throws Exception {
		leafNames = new ArrayList<String> ();
		leafNames.addAll((ArrayList<String>) cluster.getLeafNames());
		storeLeafNames = new ArrayList<String> ();
		storeLeafNames.addAll((ArrayList<String>) cluster.getLeafNames());
		previousCenters = new ArrayList<String> ();
		iterationNumber = 0;
		double [][] tempMatrix = getTempMatrix(leafNames, sequences);
		//find max in tempMatrix
		int maxI = 0;
		int maxJ = 0;
		double maxTempDistance = Integer.MIN_VALUE;
		for (int j=0; j<tempMatrix.length; j++) {
			for (int i=j; i<tempMatrix.length; i++)
				if (i != j && tempMatrix[i][j]> maxTempDistance) {
					maxTempDistance = tempMatrix[i][j];
					maxI = i;
					maxJ = j;
				}
		}
		
		tempClusters = new ArrayList <Cluster> ();
		Cluster tempCluster1 = new Cluster("Cluster#" + ++clusterNumber);
		tempCluster1.addLeafName(leafNames.get(maxI));
		previousCenters.add(leafNames.get(maxI));
		tempClusters.add(tempCluster1);
		Cluster tempCluster2 = new Cluster("Cluster#" + ++clusterNumber);
		tempCluster2.addLeafName(leafNames.get(maxJ));
		previousCenters.add(leafNames.get(maxJ));
		tempClusters.add(tempCluster2);
		
		if (maxI >= maxJ) {
			leafNames.remove(maxI);
			leafNames.remove(maxJ);
		} else {
			leafNames.remove(maxJ);
			leafNames.remove(maxI);
		}
		System.out.println("\nChosen centers:");
		for(Cluster tempCluster: tempClusters) {
			System.out.println("Center for " + tempCluster.getName() + " is: " + tempCluster.getLeafNames().get(tempCluster.getRepresentativeIndex()));
		}
		System.out.println();
		while(performIteration(sequences));
		
		System.out.println("\nCLUSTER SPLIT FINISHED!\n");
		for(Cluster c: tempClusters) {
			clusters.add(c);
		}	
	}
	
	private static double[][] getTempMatrix (ArrayList<String> sequencesSubList, ArrayList<String> sequences) throws Exception {
		double [][] tempMatrix = new double [sequencesSubList.size()][sequencesSubList.size()];
		for (int j=0; j<tempMatrix.length; j++) {
			for (int i=j; i<tempMatrix.length; i++) {
				if (i == j)
					tempMatrix[i][j] = 0;
				else {
					double linkDistance = 0;
					String firstSequence = sequencesSubList.get(i);
					String secondSequence = sequencesSubList.get(j);
					linkDistance = originalMatrix[sequences.indexOf(firstSequence)][sequences.indexOf(secondSequence)];
					tempMatrix[i][j] = linkDistance;
					tempMatrix[j][i] = linkDistance;
				}
			}
		}
		return tempMatrix;
	}
	
	private static boolean performIteration (ArrayList<String> sequences) {
		boolean flag = false;
		System.out.println("Iteration #" + ++iterationNumber + ": ");
		
		for (Cluster cluster: tempClusters) {
			String centroid = cluster.getLeafNames().get(cluster.getRepresentativeIndex());
			cluster.emptyLeafNames();
			cluster.addLeafName(centroid);
			cluster.setRepresentativeIndex(0);
		}
		
		for (String sequence: leafNames) {
			double minimumDistance = Double.MAX_VALUE;
			int index = 0;
			for (int i=0; i<2; i++) {
				Cluster currentCluster = tempClusters.get(i);
				double currentDistance = originalMatrix[sequences.indexOf(sequence)][sequences.indexOf(currentCluster.getLeafNames().get(currentCluster.getRepresentativeIndex()))];
				if (currentDistance < minimumDistance) {
					minimumDistance = currentDistance;
					index = i;
				}
			}
			tempClusters.get(index).addLeafName(sequence);
		}
		
		System.out.println();
		for (Cluster cluster: tempClusters) {
			System.out.println(cluster);
		}
		System.out.println();
		System.out.println("Caluclating new centers");
		
		leafNames = new ArrayList<String> ();
		leafNames.addAll(storeLeafNames);
		
		//calculate new centroids
		for (int i=0; i<2; i++) {
			Cluster cluster = tempClusters.get(i);
			ArrayList<String> clusterLeafNames = (ArrayList<String>) cluster.getLeafNames();
			double minAverage = Double.MAX_VALUE;
			String centroid = "";
			for (String leafName: clusterLeafNames) {
				double currentSum = 0;
				int count = 0;
				for (String otherLeafName: clusterLeafNames) {
					currentSum += originalMatrix[sequences.indexOf(leafName)][sequences.indexOf(otherLeafName)];
					count++;
				}
				double currentAverage = currentSum / count;
				if (currentAverage < minAverage) {
					minAverage = currentAverage;
					centroid = leafName;
				}
			}

			leafNames.remove(leafNames.indexOf(centroid));
			System.out.println("Center of " + cluster.getName() + " is: " + centroid);
			cluster.setRepresentativeIndex(clusterLeafNames.indexOf(centroid));
			if (!previousCenters.contains(centroid))
				flag = true;
		}
		previousCenters = new ArrayList<String>();
		for(Cluster cluster: tempClusters) {
			previousCenters.add(cluster.getLeafNames().get(cluster.getRepresentativeIndex()));
		}
		System.out.println();
		
		return flag;
	}
	
	private static double calculateVariance (Cluster cluster, ArrayList<String> sequences) {
		double variance = 0;
		String centroid = cluster.getLeafNames().get(cluster.getRepresentativeIndex());
		for (String leafName: cluster.getLeafNames()) {
			variance += Math.pow(originalMatrix[sequences.indexOf(centroid)][sequences.indexOf(leafName)], 2.0);
		}
		variance = variance / cluster.getLeafNames().size();
		return variance;
	}

}