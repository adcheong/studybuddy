/*
 *This scrapes the performance data and makes a matrix with concept as columns and user as rows
 */

import java.util.*;
import java.io.*;
 
public class ScrapePerformance {

  public void run(String filename) {
 
	BufferedReader br = null;
	String line = "";
	String idSplitBy = ",";
	int rowIndex = 0;
 
	try {
 
		HashMap<String, Integer> userIDToRow = new HashMap<String, Integer>();
 
		br = new BufferedReader(new FileReader(filename));
		while ((rowIndex <= 31890) && (line = br.readLine()) != null) {
 
			// use comma as separator
			String[] userID = line.split(idSplitBy);
 
			userIDToRow.put(userID[0], rowIndex++);
 
		}
 

		//loop map
		for (Map.Entry<String, Integer> entry : userIDToRow.entrySet()) {
 
			System.out.println("UserIDs= " + entry.getKey() + " , rowIndex="
				+ entry.getValue());
 
		}

		System.out.println(rowIndex);
 
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
 
  }
 
}