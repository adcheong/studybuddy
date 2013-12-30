/*
 *This scrapes the performance data and makes a matrix with concept as columns and user as rows
 */

import java.util.*;
import java.io.*;
 
public class ScrapePerformance {

  HashMap<String, Integer> userIDToRow;
  HashMap<String, Integer> conceptToCol;	
  double[][] performance = new double[31891][75];
  int col = 0;

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
}

	private int getRow(String userID)
	{
		int row = userIDToRow.get(userID);
		return row;
	}

	// private int getCol(String concept)
	// {
	// 	conceptToCol.get(concept);
	// }

	public void readPerformance(String filename)
	{
		BufferedReader br = null;
		String line = "";
		String idSplitBy = "\t";
 
		try {
 
		br = new BufferedReader(new FileReader(filename));
		while ((line = br.readLine()) != null) {

			if (line.charAt(0) == '[')
			{
				int i = 1;
				StringBuilder currentUserID = new StringBuilder();

				while (line.charAt(i) != ']')
				{
					currentUserID.append(line.charAt(i));
					i++;
				}

				line = br.readLine();

				// use comma as separator
				String[] score = line.split(idSplitBy);
 
				performance[getRow(currentUserID.toString())][col++] = Double.parseDouble(score[2]);
			}
 
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
  }

  public static void main(String[] args) {
 
	ScrapePerformance obj = new ScrapePerformance();
	obj.run("userIDs.csv");
	obj.runConcept("fName_to_QuizName.txt");

	obj.readPerformance("[00000062] Detailed Quiz Responses [147].txt");
 
  }
 
}