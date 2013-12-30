/*
 *This scrapes the performance data and makes a matrix with concept as columns and user as rows
 */

import java.util.*;
import java.io.*;
 
public class ScrapePerformance {

  HashMap<String, Integer> userIDToRow;
  HashMap<String, Integer> conceptToCol;	
  int[][] performance = new int[31891][75];

  public void run(String filename) {
 
	BufferedReader br = null;
	String line = "";
	String idSplitBy = ",";
	int rowIndex = 0;
 
	try {
 
		userIDToRow = new HashMap<String, Integer>();
 
		br = new BufferedReader(new FileReader(filename));
		while ((rowIndex <= 31890) && (line = br.readLine()) != null) {
 
			// use comma as separator
			String[] userID = line.split(idSplitBy);
 
			userIDToRow.put(userID[0], rowIndex++);
 
		}
 

		// //loop map
		// for (Map.Entry<String, Integer> entry : userIDToRow.entrySet()) {
 
		// 	System.out.println("UserIDs= " + entry.getKey() + " , rowIndex="
		// 		+ entry.getValue());
 
		// }

		// System.out.println(rowIndex);
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
  }

  public void runConcept(String filename) {
 
	BufferedReader br = null;
	String line = "";
	String idSplitBy = "\t";
	int colIndex = 0;
 
	try {
 
		conceptToCol = new HashMap<String, Integer>();
 
		br = new BufferedReader(new FileReader(filename));
		while ((colIndex <= 74) && (line = br.readLine()) != null) {
 
			// use comma as separator
			String[] concept = line.split(idSplitBy);
 
			conceptToCol.put(concept[1], colIndex++);
 
		}
 

		// //loop map
		// for (Map.Entry<String, Integer> entry : conceptToCol.entrySet()) {
 
		// 	System.out.println("Concept= " + entry.getKey() + " , colIndex="
		// 		+ entry.getValue());
 
		// }

		// System.out.println(colIndex);
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void getRow(String userID)
	{
		userIDToRow.get(userID);
	}

	private static void getCol(String concept)
	{
		conceptToCol.get(concept);
	}	
  }

  public static void main(String[] args) {
 
	ScrapePerformance obj = new ScrapePerformance();
	obj.run("userIDs.csv");
	obj.runConcept("fName_to_QuizName.txt");
 
  }
 
}