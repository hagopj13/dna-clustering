package com.clustering.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.clustering.visualization.Dendrogram;

public class DriverClass {

	private static Dendrogram dp;
	private static JFrame frame;
	public static Cluster clusterroot;
	final static int SINGLE_LINK = 1;
	final static int COMPLETE_LINK = 2;
	final static int AVERAGE_LINK = 3;
	final static int CENTROID_LINK = 4;

	public static void main(String[] args) throws Exception {

		frame = new JFrame();
		frame.setSize(400, 300);
		frame.setLocation(400, 300);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel content = new JPanel();
		dp = new Dendrogram();

		frame.setContentPane(content);
		content.setBackground(Color.red);
		content.setLayout(new BorderLayout());
		content.add(dp, BorderLayout.CENTER);
		dp.setBackground(Color.WHITE);

		ArrayList<String> sequences = new ArrayList<String>();
		sequences.add("A");
		sequences.add("C");
		sequences.add("T");
		sequences.add("TT");
		sequences.add("CC");
		sequences.add("NRN");
		sequences.add("RN");
		sequences.add("RAA");
		sequences.add("CCCC");
		sequences.add("TTT");
		sequences.add("CAAAA");
		sequences.add("ATG");
		// hierarchical agglomerative
		/*
		 * first param to specify sequences second param to specify the
		 * evaluation method third param to specify the threshold (0.0 = no
		 * threshold)
		 */
		// doHierarchical(sequences, AVERAGE_LINK, 0.0);

		// k means
		/*
		 * first param to specify sequences second param to specify the number
		 * of clusters k
		 */
		// doKMeans(sequences, 3);

		// buckshot
		/*
		 * first param to specify sequences second param to specify the number
		 * of clusters k
		 */
		//doBuckshot(sequences, 3);

	}

	public static void doHierarchical(ArrayList<String> sequences, int evaluationMethod, double threshold)
			throws Exception {
		Cluster cluster = AgglomerativeHierarchical.performHierarchicalClustering(sequences, evaluationMethod,
				threshold, 0);
		clusterroot = cluster;
		// dp.setRoot(cluster);
		// frame.setVisible(true);
	}

	public static ArrayList<Cluster> doKMeans(ArrayList<String> sequences, int k) throws Exception {
		ArrayList<Cluster> clusters = KMeansClustering.performKMeansClustering(sequences, k);
		return clusters;
	}

	public static ArrayList<Cluster> doBuckshot(ArrayList<String> sequences, int k) throws Exception {
		int n = sequences.size();
		int sampleSize = (int) Math.ceil(Math.sqrt(k * n));
		ArrayList<String> sample = new ArrayList<String>();
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.addAll(sequences);
		Random rnd = new Random();
		System.out.println("Randomly selected sample:");
		for (int i = 0; i < sampleSize; i++) {
			int index = rnd.nextInt(tempList.size());
			sample.add(tempList.get(index));
			System.out.print(tempList.get(index) + " ");
			tempList.remove(index);
		}
		System.out.println("\n");
		Cluster cluster = AgglomerativeHierarchical.performHierarchicalClustering(sample, AVERAGE_LINK, 0.0, k);
		ArrayList<Cluster> clusters = KMeansClustering.performBuckshotClustering(sequences, k, cluster);
		clusterroot = cluster;
		return clusters;
		// dp.setRoot(cluster);
		// frame.setVisible(true);
	}
	
	public static ArrayList<Cluster> doMBKM (ArrayList<String> sequences, int k) throws Exception {
		ArrayList<Cluster> clusters = MBKM.performMBKMClustering(sequences, k);
		return clusters;
	}

}
