package com.clustering.main;

import java.util.ArrayList;
import java.util.Random;

public class KMeansClustering {
	
	static double [][] originalMatrix;
	static ArrayList<String> previousCenters;
	static ArrayList<Cluster> clusters;
	static ArrayList<String> remainingSequences;
	static int iterationNumber = 0;
	
	public static ArrayList<Cluster> performKMeansClustering(ArrayList<String> sequences, int k) throws Exception {
		if (sequences.size() < k)
			throw new Exception("The number of sequences is less than k");
		iterationNumber = 0;
		clusters = new ArrayList<Cluster> ();
		previousCenters = new ArrayList<String> ();
		originalMatrix = SequenceEdit.getOriginalMatrix(sequences);
		remainingSequences = new ArrayList<String> ();
		remainingSequences.addAll(sequences);
		System.out.println("Randomly selected centers: ");
		for (int i=0; i<k; i++) {
			Random rnd = new Random();
			int index = rnd.nextInt(remainingSequences.size());
			Cluster newCluster = new Cluster("Cluster#" + (i+1));
			newCluster.addLeafName(remainingSequences.get(index));
			System.out.println("Center " + (i+1) + ": " + remainingSequences.get(index));
			previousCenters.add(remainingSequences.get(index));
			remainingSequences.remove(index);
			clusters.add(newCluster);
		}
		System.out.println();
		System.out.println("----------------");
		System.out.println();
		
		while (performIteration(sequences, k));
		
		System.out.println();
		System.out.println("---------------------");
		System.out.println("Stop");
		
		return clusters;
	}
	
	public static ArrayList<Cluster> performBuckshotClustering(ArrayList<String> sequences, int k, Cluster rootCluster) throws Exception {
		if (sequences.size() < k)
			throw new Exception("The number of sequences is less than k");
		iterationNumber = 0;
		clusters = new ArrayList<Cluster> ();
		previousCenters = new ArrayList<String> ();
		originalMatrix = SequenceEdit.getOriginalMatrix(sequences);
		remainingSequences = new ArrayList<String> ();
		remainingSequences.addAll(sequences);
		System.out.println("\n**********************************************************\n");
		System.out.println("Centers selected after agglomerative hierarchical clustering:");
		ArrayList<Cluster> initialClusters = new ArrayList<Cluster> ();
		initialClusters.addAll(rootCluster.getChildren().get(0).getChildren());
		for (int i=0; i<k; i++) {
			Cluster newCluster = new Cluster("Cluster#" + (i+1));
			Cluster initialCluster = initialClusters.get(i);
			String initialCenter = initialCluster.getLeafNames().get(initialCluster.getRepresentativeIndex());
			newCluster.addLeafName(initialCenter);
			System.out.println("Center " + (i+1) + ": " + initialCenter);
			previousCenters.add(initialCenter);
			remainingSequences.remove(remainingSequences.indexOf(initialCenter));
			clusters.add(newCluster);
		}
		
		System.out.println();
		System.out.println("----------------");
		System.out.println();
		
		while (performIteration(sequences, k));
		
		System.out.println();
		System.out.println("---------------------");
		System.out.println("Stop");
		
		return clusters;
	}
	
	private static boolean performIteration (ArrayList<String> sequences, int k) {
		boolean flag = false;
		System.out.println("Iteration #" + ++iterationNumber + ": ");
		
		for (Cluster cluster: clusters) {
			String centroid = cluster.getLeafNames().get(cluster.getRepresentativeIndex());
			cluster.emptyLeafNames();
			cluster.addLeafName(centroid);
			cluster.setRepresentativeIndex(0);
		}
		
		for (String sequence: remainingSequences) {
			double minimumDistance = Double.MAX_VALUE;
			int index = 0;
			for (int i=0; i<k; i++) {
				Cluster currentCluster = clusters.get(i);
				double currentDistance = originalMatrix[sequences.indexOf(sequence)][sequences.indexOf(currentCluster.getLeafNames().get(currentCluster.getRepresentativeIndex()))];
				if (currentDistance < minimumDistance) {
					minimumDistance = currentDistance;
					index = i;
				}
			}
			clusters.get(index).addLeafName(sequence);
		}
		
		System.out.println();
		for (Cluster cluster: clusters) {
			System.out.println(cluster);
		}
		System.out.println();
		System.out.println("Caluclating new centers");
		
		remainingSequences = new ArrayList<String> ();
		remainingSequences.addAll(sequences);
		
		//calculate new centroids
		for (int i=0; i<k; i++) {
			Cluster cluster = clusters.get(i);
			ArrayList<String> leafNames = (ArrayList<String>) cluster.getLeafNames();
			double minAverage = Double.MAX_VALUE;
			String centroid = "";
			for (String leafName: leafNames) {
				double currentSum = 0;
				int count = 0;
				for (String otherLeafName: leafNames) {
					currentSum += originalMatrix[sequences.indexOf(leafName)][sequences.indexOf(otherLeafName)];
					count++;
				}
				double currentAverage = currentSum / count;
				if (currentAverage < minAverage) {
					minAverage = currentAverage;
					centroid = leafName;
				}
			}
			remainingSequences.remove(remainingSequences.indexOf(centroid));
			System.out.println("Center of " + cluster.getName() + " is: " + centroid);
			cluster.setRepresentativeIndex(leafNames.indexOf(centroid));
			if (!previousCenters.contains(centroid))
				flag = true;
		}
		
		previousCenters = new ArrayList<String>();
		for(Cluster cluster: clusters) {
			previousCenters.add(cluster.getLeafNames().get(cluster.getRepresentativeIndex()));
		}
		
		System.out.println();
		
		return flag;
	}

}
