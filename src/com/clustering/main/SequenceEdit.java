package com.clustering.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SequenceEdit {
	
	static Map <String, String> symbols;
	final static String validCharacters = "ACGTRYKMSWBDHVN";
	final static String validNucleotides = "ACTG";
	
	public static double [][] getOriginalMatrix (ArrayList<String> sequences) throws Exception {
		symbols = new HashMap <String, String> ();
		symbols.put("A", "A");
		symbols.put("T", "T");
		symbols.put("G", "G");
		symbols.put("C", "C");
		symbols.put("R", "AG");
		symbols.put("Y", "TC");
		symbols.put("K", "GT");
		symbols.put("M", "AC");
		symbols.put("S", "GC");
		symbols.put("W", "AT");
		symbols.put("B", "GTC");
		symbols.put("D", "GAT");
		symbols.put("H", "ACT");
		symbols.put("V", "GCA");
		symbols.put("N", "ATCG");
		
		double [][] originalMatrix = new double [sequences.size()][sequences.size()];
		for (int j=0; j<sequences.size(); j++) {
			for (int i=j; i<sequences.size(); i++) {
				double editDistance = getWFEditDistance(sequences.get(i), sequences.get(j));
				originalMatrix[i][j] = editDistance;
				originalMatrix[j][i] = editDistance;
			}
		}
		return originalMatrix;
	}
	
	private static double getWFEditDistance (String str1, String str2) throws Exception {
		int len1 = str1.length();
        int len2 = str2.length();
        double [][] arr = new double[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) {
            arr[i][0] = i;
        }
        for (int i = 1; i <= len2; i++) {
            arr[0][i] = i;
        }
        for (int i = 1; i <= len1; i++){
            for (int j = 1; j <= len2; j++) {
                double updateCost = getUpdateCost(str1.charAt(i-1), str2.charAt(j-1));                        
                arr[i][j] = Math.min(Math.min(arr[i - 1][j] + 1, arr[i][j - 1] + 1), arr[i - 1][j - 1] + updateCost);
            }
        }
        return arr[len1][len2];
	}
	
	private static double getUpdateCost (char char1, char char2) throws Exception {
		if (validCharacters.indexOf(char1) == (-1))
			throw new Exception ("Invalid symbol in the first DNA Sequence"); 
		if (validCharacters.indexOf(char2) == (-1))
			throw new Exception ("Invalid symbol in the second DNA Sequence"); 
		if (char1 == char2)
			return 0;
		String symbol1 = symbols.get(char1 + "");
		String symbol2 = symbols.get(char2 + "");
		int hits = 0, total = symbol1.length() + symbol2.length();
		for (int i=0; i<symbol1.length(); i++) {
			if (symbol2.indexOf(symbol1.charAt(i)) >= 0)
				hits++;
		}
		return (1 - (hits * 2) / (double) total);
	}
	

}
