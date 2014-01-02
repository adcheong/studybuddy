/*
 *This scrapes the performance data and makes a matrix with concept as columns and user as rows
 */

import java.util.*;
import java.io.*;
 
public class ScrapePerformance {

  HashMap<String, Integer> userIDToRow;
  HashMap<String, Integer> conceptToCol;	
  double[][] performance = new double[31891][69];
  int col = 0;

  public ScrapePerformance() {
  	for (int i = 0; i < 31891; i++)
  	{
  		for (int j = 0; j < 69; j++)
  			performance[i][j] = -1.0;
  	}
  }

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

	public void readPerformance(String filename)
	{
		BufferedReader br = null;
		String line;
		String idSplitBy = "\t";
 
		try {
 
		br = new BufferedReader(new FileReader(filename));

		while (br.ready()) {

			line = br.readLine();
			//System.out.println(line);

			if (line.equals(""))
				continue;
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

				if (line.equals(""))
					continue;

				// use comma as separator
				String[] score = line.split(idSplitBy);
 				//System.out.println(currentUserID);

				performance[getRow(currentUserID.toString())][col] = Double.parseDouble(score[2]);
			}
 
		}
 
		col++;
 
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

  public void printMatrix(String filename)
  {
  	try
  	{
	  	FileWriter writer = new FileWriter(filename);

	  	writer.append(String.valueOf(31891) + '\n');
	  	writer.append(String.valueOf(69) + '\n');

	  	for (int i = 0; i < 31891; i++)
	  	{
	  		for (int j = 0; j < 69; j++)
	  		{
	  			writer.append(String.valueOf(performance[i][j]) + ",");
	  		}

	  		writer.append('\n');
	  	}

	  	writer.close();
	}

	catch(IOException e)
	{
		e.printStackTrace();
	}
  }

  public static void main(String[] args) {
 
	ScrapePerformance obj = new ScrapePerformance();
	obj.run("userIDs.csv");

	obj.readPerformance("[00000062] Detailed Quiz Responses [147].txt");
	obj.readPerformance("[00000063] Detailed Quiz Responses [201].txt");
	obj.readPerformance("[00000071] Detailed Quiz Responses [53].txt");
	obj.readPerformance("[00000072] Detailed Quiz Responses [57].txt");
	obj.readPerformance("[00000074] Detailed Quiz Responses [61].txt");
	obj.readPerformance("[00000077] Detailed Quiz Responses [65].txt");
	obj.readPerformance("[00000078] Detailed Quiz Responses [67].txt");
	obj.readPerformance("[00000080] Detailed Quiz Responses [71].txt");
	obj.readPerformance("[00000082] Detailed Quiz Responses [75].txt");
	obj.readPerformance("[00000083] Detailed Quiz Responses [77].txt");
	obj.readPerformance("[00000086] Detailed Quiz Responses [83].txt");
	obj.readPerformance("[00000089] Detailed Quiz Responses [89].txt");
	obj.readPerformance("[00000091] Detailed Quiz Responses [97].txt");
	obj.readPerformance("[00000094] Detailed Quiz Responses [103].txt");
	obj.readPerformance("[00000095] Detailed Quiz Responses [105].txt");
	obj.readPerformance("[00000096] Detailed Quiz Responses [107].txt");
	obj.readPerformance("[00000097] Detailed Quiz Responses [109].txt");
	obj.readPerformance("[00000098] Detailed Quiz Responses [111].txt");
	obj.readPerformance("[00000099] Detailed Quiz Responses [115].txt");
	obj.readPerformance("[00000100] Detailed Quiz Responses [117].txt");
	obj.readPerformance("[00000102] Detailed Quiz Responses [121].txt");
	obj.readPerformance("[00000103] Detailed Quiz Responses [123].txt");
	obj.readPerformance("[00000105] Detailed Quiz Responses [133].txt");
	obj.readPerformance("[00000106] Detailed Quiz Responses [135].txt");
	obj.readPerformance("[00000107] Detailed Quiz Responses [137].txt");
	obj.readPerformance("[00000108] Detailed Quiz Responses [139].txt");
	obj.readPerformance("[00000109] Detailed Quiz Responses [141].txt");
	obj.readPerformance("[00000111] Detailed Quiz Responses [143].txt");
	obj.readPerformance("[00000112] Detailed Quiz Responses [145].txt");
	obj.readPerformance("[00000115] Detailed Quiz Responses [155].txt");
	obj.readPerformance("[00000116] Detailed Quiz Responses [157].txt");
	obj.readPerformance("[00000117] Detailed Quiz Responses [159].txt");
	obj.readPerformance("[00000118] Detailed Quiz Responses [161].txt");
	obj.readPerformance("[00000119] Detailed Quiz Responses [165].txt");
	obj.readPerformance("[00000120] Detailed Quiz Responses [167].txt");
	obj.readPerformance("[00000121] Detailed Quiz Responses [169].txt");
	obj.readPerformance("[00000123] Detailed Quiz Responses [173].txt");
	obj.readPerformance("[00000124] Detailed Quiz Responses [175].txt");
	obj.readPerformance("[00000126] Detailed Quiz Responses [183].txt");
	obj.readPerformance("[00000127] Detailed Quiz Responses [185].txt");
	obj.readPerformance("[00000128] Detailed Quiz Responses [187].txt");
	obj.readPerformance("[00000130] Detailed Quiz Responses [191].txt");
	obj.readPerformance("[00000131] Detailed Quiz Responses [193].txt");
	obj.readPerformance("[00000132] Detailed Quiz Responses [197].txt");
	obj.readPerformance("[00000133] Detailed Quiz Responses [199].txt");
	obj.readPerformance("[00000134] Detailed Quiz Responses [203].txt");
	obj.readPerformance("[00000135] Detailed Quiz Responses [207].txt");
	obj.readPerformance("[00000136] Detailed Quiz Responses [209].txt");
	obj.readPerformance("[00000137] Detailed Quiz Responses [211].txt");
	obj.readPerformance("[00000141] Detailed Quiz Responses [215].txt");
	obj.readPerformance("[00000143] Detailed Quiz Responses [223].txt");
	obj.readPerformance("[00000145] Detailed Quiz Responses [227].txt");
	obj.readPerformance("[00000148] Detailed Quiz Responses [233].txt");
	obj.readPerformance("[00000149] Detailed Quiz Responses [235].txt");
	obj.readPerformance("[00000150] Detailed Quiz Responses [237].txt");
	obj.readPerformance("[00000152] Detailed Quiz Responses [241].txt");
	obj.readPerformance("[00000153] Detailed Quiz Responses [243].txt");
	obj.readPerformance("[00000154] Detailed Quiz Responses [245].txt");
	obj.readPerformance("[00000157] Detailed Quiz Responses [251].txt");
	obj.readPerformance("[00000162] Detailed Quiz Responses [261].txt");
	obj.readPerformance("[00000163] Detailed Quiz Responses [263].txt");
	obj.readPerformance("[00000166] Detailed Quiz Responses [269].txt");
	obj.readPerformance("[00000167] Detailed Quiz Responses [271].txt");
	obj.readPerformance("[00000168] Detailed Quiz Responses [273].txt");
	obj.readPerformance("[00000169] Detailed Quiz Responses [275].txt");
	obj.readPerformance("[00000170] Detailed Quiz Responses [277].txt");
	obj.readPerformance("[00000173] Detailed Quiz Responses [285].txt");
	obj.readPerformance("[00000175] Detailed Quiz Responses [287].txt");
	obj.readPerformance("[00000177] Detailed Quiz Responses [291].txt");

	obj.printMatrix("performancematrix.csv");
 
  }
 
}