/*
 * @author : Mir Mohammed Mehdi
 * Student ID :185808970
 * Reference Credits : 1. https://www.geeksforgeeks.org/print-all-possible-combinations-of-r-elements-in-a-given-array-of-size-n/
 * 					   2. https://stackoverflow.com/questions/1786206/is-there-a-java-equivalent-of-pythons-defaultdict
 */

package com.da.asingment1;

import java.io.*;
import java.util.*;

public class Apriori {

	public static List<String> TwinsList = new ArrayList<String>();

	public static List<String> TripletList = new ArrayList<String>();

	public static List<String[]> fileInObject = new ArrayList<String[]>();
	
	public static DefaultDict<List<String>, Integer> finalDictionary = new DefaultDict<List<String>, Integer>(ArrayList.class);

	public static DefaultDict<List<String>, Integer> twinDictionary = new DefaultDict<List<String>, Integer>(
			ArrayList.class);

	public static void main(String[] args) throws FileNotFoundException {

		String fileName = "C:\\Users\\MIR\\Documents\\Term 2\\Data Analytics\\Assing1 file\\browsing.txt";
		// open the file

		Scanner input = new Scanner(new File(fileName));
		int minSupport = 100;
		List<String> newlist = new ArrayList<String>();
		HashSet<String> TwinHash = new HashSet<>();
		HashSet<List<String>> TripletHash = new HashSet<>();

		String[] strArr;

		// count occurrences
		
		while (input.hasNext()) {
			String next = input.next();
			List<String> itemList = new ArrayList<String>();
			itemList.add(next);
			if (!finalDictionary.containsKey(itemList)) {
				finalDictionary.put(itemList, 1);
			} else {
				finalDictionary.put(itemList, finalDictionary.get(itemList) + 1);

			}
		}
		input.close();

		// Level 2 of Apriori Starts
		Scanner inputline = new Scanner(new File(fileName));

		while (inputline.hasNextLine()) {
			String next = inputline.nextLine();
			String[] lineArray = next.split(" ");
			fileInObject.add(lineArray);

			for (int i = 0; i < lineArray.length; i++) {
				List<String> itemList = new ArrayList<String>();
				itemList.add(lineArray[i]);
				if (finalDictionary.get(itemList) >= minSupport) {
					newlist.add(lineArray[i]);// To avoid duplication
				}
				TwinHash.addAll(newlist);
			}
		}

		inputline.close();

		strArr = new String[TwinHash.size()];
		TwinHash.toArray(strArr);

		// List of Order 2
		twinCombinations(strArr, strArr.length, 2);

		for (int k = 0; k <= TwinsList.size() - 2; k = k + 2) {
			List<String> itemList = new ArrayList<String>();
			itemList.add(TwinsList.get(k));
			itemList.add(TwinsList.get(k + 1));
			addToDictionary(itemList);
		}
		
		// Pruning the dataset by removing the entries with frequency less than
		// the support value - Starts
		for (Iterator<Map.Entry<List<String>, Integer>> it = twinDictionary.entrySet().iterator(); it.hasNext();) {
			Map.Entry<List<String>, Integer> entry = it.next();
			if (entry.getValue() < minSupport) {
				it.remove();
			}
		}
		// Pruning the dataset by removing the entries with frequency less than
		// the support value - Ends
		
		// Level 2 of Apriori Ends

		// Level 3 of Apriori Starts
		List<List<String>> twinlist = new ArrayList<List<String>>();

		for (List<String> word : twinDictionary.keySet()) {
			if (twinDictionary.get(word) >= minSupport) {
				twinlist.add(word);
			}
		}

		// List of Order 3
		tripletCombinations(twinlist);

		for (int k = 0; k <= TripletList.size() - 3; k = k + 3) {
			List<String> itemList = new ArrayList<String>();
			itemList.add(TripletList.get(k));
			itemList.add(TripletList.get(k + 1));
			itemList.add(TripletList.get(k + 2));
			if (!TripletHash.contains(itemList)) {
				TripletHash.add(itemList); // To avoid duplication
				addToDictionary(itemList);
			}
		}
		// Level 3 of Apriori Ends

		// Pruning the dataset by removing the entries with frequency less than
		// the support value - Starts
		for (Iterator<Map.Entry<List<String>, Integer>> it = finalDictionary.entrySet().iterator(); it.hasNext();) {
			Map.Entry<List<String>, Integer> entry = it.next();
			if (entry.getValue() < minSupport) {
				it.remove();
			}
		}
		System.out.println(finalDictionary);
		// Pruning the dataset by removing the entries with frequency less than
		// the support value - Ends
		
		
		// Calculation of Confidence Score starts
				List<Result> resultList = new ArrayList<Result>();
				List<Result> tripletresultList = new ArrayList<Result>();
				for (List<String> word : finalDictionary.keySet()) {
					int count = finalDictionary.get(word);
					if (count >= minSupport && word.size() == 2) {
						for (List<String> singleWord : finalDictionary.keySet()) {
							int single_count = finalDictionary.get(singleWord);
							if (single_count >= minSupport) {
								if (singleWord.size() == 1) {
									if (word.contains(singleWord.get(0))) // calculating the confidence score for item pairs
										resultList = addResultObj(resultList, word, singleWord, single_count, count, 
												singleWord.size());
								} else if (singleWord.size() == 3) { // calculating the confidence score for item triplets
									if (singleWord.contains(word.get(0)) && singleWord.contains(word.get(1)))
										tripletresultList = addResultObj(tripletresultList, word, singleWord, count,
												single_count, singleWord.size());
								}
							}
						}
					}
				}
				// Calculation of Confidence Score ends

				Result[] finalTwinresultList = new Result[resultList.size()]; // Converting the List to Sorted Array - for item pairs
				finalTwinresultList = listToSortedArray(finalTwinresultList, resultList);

				Result[] finalTripletresultList = new Result[tripletresultList.size()]; // Converting the List to Sorted Array - for item triplets
				finalTripletresultList = listToSortedArray(finalTripletresultList, tripletresultList);

				// Printing the top 5 for item pair and triplets starts
				printFinalList(finalTwinresultList);
				printFinalList(finalTripletresultList);
				// Printing the top 5 for item pair and triplets ends

				System.out.println("End of Program");
	}
	
	public static List<Result> addResultObj(List<Result> resList, List<String> word, List<String> singleWord, int count,
			int single_count, int size) {
		Result objResult = new Result();
		objResult.setSingleWord(singleWord);
		objResult.setWord(word);
		if (size == 2) {
			objResult.setConfidenceScore((double) count / single_count * 100);
		} else {
			objResult.setConfidenceScore((double) single_count / count * 100);
		}
		System.out.println("Confidence score of " + objResult.getWord() + " => "
				+ objResult.getSingleWord() + " is " + objResult.getConfidenceScore() + "%");
		resList.add(objResult);
		return resList;
	}

	public static Result[] listToSortedArray(Result[] finalResArray, List<Result> resList) {

		finalResArray = resList.toArray(finalResArray);

		for (int x = 1; x < finalResArray.length; x++) {
			Result res_Key = finalResArray[x];
			int y = x - 1;
			while (y >= 0 && finalResArray[y].getConfidenceScore() < res_Key.getConfidenceScore()) {
				finalResArray[y + 1] = finalResArray[y];
				y--;
			}
			finalResArray[y + 1] = res_Key;
		}
		return finalResArray;
	}

	public static void printFinalList(Result[] finalresultList) {
		System.out.println("---------------------Printing the final top 5----------------------");
		for (int i = 0; i < 5; i++) {
			System.out.println("Confidence score of " + finalresultList[i].getWord() + " => "
					+ finalresultList[i].getSingleWord() + " is " + finalresultList[i].getConfidenceScore() + "%");
		}
	}

	static void combinationUtil(String[] strArr, String data[], int start, int end, int index, int r) {
		// Current combination is ready to be printed, print it
		if (index == r) {
			for (int j = 0; j < r - 1; j++) {
				TwinsList.add(data[j]);
				TwinsList.add(data[j + 1]);
			}
			return;
		}

		// replace index with all possible elements. The condition
		// "end-i+1 >= r-index" makes sure that including one element
		// at index will make a combination with remaining elements
		// at remaining positions
		for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
			data[index] = (strArr[i]);
			combinationUtil(strArr, data, i + 1, end, index + 1, r);
		}
	}

	// The main function that prints all combinations of size r
	// in arr[] of size n. This function mainly uses combinationUtil()
	static void twinCombinations(String[] strArr, int n, int r) {
		// A temporary array to store all combination one by one
		String data[] = new String[r];

		// Print all combination using temprary array 'data[]'
		combinationUtil(strArr, data, 0, n - 1, 0, r);
	}

	static void tripletCombinations(List<List<String>> pairlist) {
		for (int i = 0; i < pairlist.size(); i++) {
			for (int j = i + 1; j < pairlist.size(); j++) {
				if (!pairlist.get(i).contains(pairlist.get(j).get(0))) {
					TripletList.add(pairlist.get(i).get(0));
					TripletList.add(pairlist.get(i).get(1));
					TripletList.add(pairlist.get(j).get(0));
				}
				if (!pairlist.get(i).contains(pairlist.get(j).get(1))) {
					TripletList.add(pairlist.get(i).get(0));
					TripletList.add(pairlist.get(i).get(1));
					TripletList.add(pairlist.get(j).get(1));
				}
			}
		}
	}

	public static void addToDictionary(List<String> items) {
		for (int i = 0; i < fileInObject.size(); i++) {
			String[] tem = fileInObject.get(i).clone();
			int condcount = 0;
			for (int kl = 0; kl < tem.length; kl++) {
				if (items.contains(tem[kl])) {
					condcount++;
				}
			}
			if (condcount == items.size()) {
				if (!finalDictionary.containsKey(items)) {
					finalDictionary.put(items, 1);
					if(items.size()==2){
						twinDictionary.put(items, 1);;
					}
				} else {
					finalDictionary.put(items, finalDictionary.get(items) + 1);
					if(items.size()==2){
						twinDictionary.put(items, finalDictionary.get(items) + 1);
					}
				}
			}
		}
	}

}
