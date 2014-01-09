/* Latent Factor Model Code */

import java.util.*;
import java.io.*;
import java.net.*;

public class LFM {

    private class topic
    {
        public String name;
        public HashSet<Integer> ids;
        public topic(String fname, HashSet<Integer> user_ids)
        {
            name = fname;
            ids = user_ids;
        }
    }

    private HashMap<Integer, Integer> colMap;
    private double performanceScaleFactor;

    public double[][] getMatrix (String filename) throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        int row = Integer.parseInt(in.readLine());
        int col = Integer.parseInt(in.readLine());

        double[][] out = new double[row][col];

        for (int r = 0; r < row; r++)
        {
            StringTokenizer st = new StringTokenizer(in.readLine(), ",");
            for (int c = 0; c < col; c++)
                out[r][c] = Double.parseDouble(st.nextToken());
        }
        return out;
    }

    public double[][] mergeMatrix(double[][] a, double[][] b, double ascale, double bscale)
    {
        int row = a.length;
        int col = a[0].length;
        double[][] out = new double[row][col];
        performanceScaleFactor = bscale;

        for(int i = 0; i < row; i++)
            for(int j = 0; j < col; j++)
                out[i][j] = ascale*a[i][j] + bscale*b[i][j];
        return out;
    }

    /* threshold indicates the percentage of questions a user must attempt to 
     * to be considered as an active user */
    public boolean[] getActiveRows(double[][] performance, double threshold)
    {
        int rows = performance.length;
        int cols = performance[0].length;
        boolean[] active = new boolean[rows];

        for (int i = 0; i < rows; i++)
        {
            // double count = 0.0;
            for (int j = 0; j < cols; j++)
            {
                if (performance[i][j] >= 0)
                {
                    active[i] = true;
                    break;
                }
            }
            //active[i] = count / cols >= threshold;
        }
        
        return active;
    }

    public void getColumnMapping(String filename) throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        colMap = new HashMap<Integer, Integer>();
        StringTokenizer st;
        int index = 0;
        while(in.ready())
        {
            st = new StringTokenizer(in.readLine());
            colMap.put(Integer.parseInt(st.nextToken()), index++);
        }
    }

    public String[] getUserIds() throws IOException
    {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new FileReader("userIDs.csv"));

        while(in.ready())
        {
            String line = in.readLine();
            if (line.charAt(0) == ',')
                break;
            StringTokenizer st = new StringTokenizer(line, ",");
            list.add(st.nextToken());
        }

        String[] output = new String[list.size()];
        output = list.toArray(output);
        return output;
    }

    public double[][] removeInactiveRows(double[][] vals, boolean[] active)
    {
        int newRows = 0;
        int curRow = 0;
        for (int i = 0; i < active.length; i++)
            if (active[i]) newRows++;

        double[][] activeData = new double[newRows][vals[0].length];

        for (int i = 0; i < active.length; i++)
            if (active[i])
            {
                for (int j = 0; j < vals[0].length; j++)
                    activeData[curRow][j] = vals[i][j];
                curRow++;
            }
        return activeData;
    }

    public topic[] getTopics(String filename) throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        ArrayList<topic> topics = new ArrayList<topic>();
        int numTopics = 0;
        while(in.ready())
        {
            String name = in.readLine();
            String line = in.readLine();
            HashSet<Integer> ids = new HashSet<Integer>();
            while(!line.equals(""))
            {
                ids.add(Integer.parseInt(line));
                line = in.readLine();
            }
            topics.add(new topic(name, ids));
            numTopics++;
        }
        return topics.toArray(new topic[numTopics]);
    }

    public double[][] mergeColumns (topic[] topics, double[][] matrix)
    {
        int numTopics = topics.length;
        int rows = matrix.length;
        double[][] merged = new double[rows][numTopics];
        int[][] mergeCount = new int[rows][numTopics];

        for(int i = 0; i < rows; i++)
            for(int j = 0; j < numTopics; j++)
            {
                merged[i][j] = 0.0;
                mergeCount[i][j] = 0;
            }

        for (int i = 0; i < numTopics; i++)
            for (int id : topics[i].ids)
                for(int r = 0; r < rows; r++)
                    // BE CAUTIOUS OF THIS MAGIC NUMBER
                {
                    double val = matrix[r][colMap.get(id)];
                    if (val != -1 * performanceScaleFactor)
                    {
                        merged[r][i] += val < 0.0 ? val + performanceScaleFactor : val;
                        mergeCount[r][i]++;
                    }
                }

        for(int i = 0; i < rows; i++)
            for(int j = 0; j < numTopics; j++)
                if (mergeCount[i][j] != 0.0)
                    merged[i][j] /= mergeCount[i][j];
        return merged;
    }


    public double[][] mergeColumns2 (topic[] topics, double[][] matrix)
    {
        int numTopics = topics.length;
        int rows = matrix.length;
        double[][] merged = new double[rows][numTopics];
        int[][] mergeCount = new int[rows][numTopics];

        for(int i = 0; i < rows; i++)
            for(int j = 0; j < numTopics; j++)
            {
                merged[i][j] = 0.0;
                mergeCount[i][j] = 0;
            }

        for (int i = 0; i < numTopics; i++)
            for (int id : topics[i].ids)
                for(int r = 0; r < rows; r++)
                    // BE CAUTIOUS OF THIS MAGIC NUMBER
                {
                    double val = matrix[r][colMap.get(id)];
                    if (val != -1)
                    {
                        merged[r][i] += val;
                        mergeCount[r][i]++;
                    }
                }

        for(int i = 0; i < rows; i++)
            for(int j = 0; j < numTopics; j++)
                if (mergeCount[i][j] != 0.0)
                    merged[i][j] /= mergeCount[i][j];
        return merged;
    }

    public double[][] mergeColumns3 (topic[] topics, double[][] matrix)
    {
        int numTopics = topics.length;
        int rows = matrix.length;
        double[][] merged = new double[rows][numTopics];
        int[][] mergeCount = new int[rows][numTopics];

        for(int i = 0; i < rows; i++)
            for(int j = 0; j < numTopics; j++)
            {
                merged[i][j] = 0.0;
                mergeCount[i][j] = 0;
            }

        for (int i = 0; i < numTopics; i++)
            for (int id : topics[i].ids)
                for(int r = 0; r < rows; r++)
                    // BE CAUTIOUS OF THIS MAGIC NUMBER
                {
                    double val = matrix[r][colMap.get(id)];
                    if (val != 0)
                    {
                        merged[r][i] += val;
                        mergeCount[r][i]++;
                    }
                }

        for(int i = 0; i < rows; i++)
            for(int j = 0; j < numTopics; j++)
                if (mergeCount[i][j] != 0.0)
                    merged[i][j] /= mergeCount[i][j];
        return merged;
    }


    public static double dotProduct (double[][] m, int a, int b)
    {
        double val = 0;
        int len = m[0].length;
        for(int i = 0; i < len; i++)
            val += m[a][i]*m[b][i];
        return val;
    }

    public static double magnitude(double[][] m, int a)
    {
        double val = 0;
        int len = m[0].length;
        for(int i = 0; i < len; i++)
            val += m[a][i]*m[a][i];
        return Math.sqrt(val);
    }

    public static double[][] cosineCoef(double[][] matrix)
    {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[rows][rows];

        /* can save space using an arraylist of arrays */
        /* will implement later if needed */
        for(int i = 0; i < rows; i++)
            for(int j = i+1; j < rows; j++)
            {
                double dp = dotProduct(matrix, i, j);
                double mag_i = magnitude(matrix, i);
                double mag_j = magnitude(matrix, j);
                result[i][j] = mag_i * mag_j <= 0 ? -2 : dp / (mag_i * mag_j);
            }
        return result;    
    }

    public static ArrayList<int[]> getPairs(double[][] matrix)
    {
        ArrayList<int[]> output = new ArrayList<int[]>();
        for(int i = 0; i < matrix.length; i++)
            for(int j = 0; j < matrix[0].length; j++)
            {
                if (matrix[i][j] < 0.075 && matrix[i][j] > -2 && matrix[i][j] != 0.0)
                {
                    int[] pair = {i, j};
                    output.add(pair);
                }
            }
        return output;
    }
    public static ArrayList<int[]> getPairs2(double[][] matrix)
    {
        ArrayList<int[]> output = new ArrayList<int[]>();
        for(int i = 0; i < matrix.length; i++)
            for(int j = 0; j < matrix[0].length; j++)
            {
                if (matrix[i][j] < 0.075 && matrix[i][j] < 1 && matrix[i][j] > 0.0)
                {
                    int[] pair = {i, j};
                    output.add(pair);
                }
            }
        return output;
    }

    public static ArrayList<int[]> getPairs3(double[][] matrix)
    {
        ArrayList<int[]> output = new ArrayList<int[]>();
        for(int i = 0; i < matrix.length; i++)
            for(int j = 0; j < matrix[0].length; j++)
            {
                if (matrix[i][j] < 0.3 && matrix[i][j] > -2 && matrix[i][j] != 0.0)
                {
                    int[] pair = {i, j};
                    output.add(pair);
                }
            }
        return output;
    }

    public static void main(String[] args) throws IOException 
    {
        LFM lfm = new LFM();

        /* Basic I/O */
        lfm.getColumnMapping("fName_to_QuizName.txt");
        double[][] forumData   = lfm.getMatrix("forum_matrix.csv");
        double[][] performData = lfm.getMatrix("performancematrix.csv");
        String[] uids = lfm.getUserIds();

        /* Extracting active data */
        boolean[] activeUids = lfm.getActiveRows(performData, 0.0);
        double[][] activePerf = lfm.removeInactiveRows(performData, activeUids);
        double[][] activeForum = lfm.removeInactiveRows(forumData, activeUids);        
        double[][] activeData   = lfm.mergeMatrix(activeForum, activePerf, 1.0, 3.0);

        // for (int i = 0; i < activeData.length; i++)
        // {
        //     for(int j = 0; j< activeData[0].length; j++)
        //         System.out.print(activeData[i][j] + " ");
        //     System.out.println();
        // }

        // System.out.println("Number of rows: " + activeData.length);
        // System.out.println("Number of columns: " + activeData[0].length);

        //=====================================================================
        //=========================MERGED DATA=================================
        //=====================================================================

        /* Merging the active data so the columns will represent each topic */
        /* Also computing the cosine coefficient and getting the minimum coefficient pairs */

        topic[] topics = lfm.getTopics("topics.txt");
        // double[][] finalData = lfm.mergeColumns(topics, activeData);
        // finalData = lfm.cosineCoef(finalData);
        // ArrayList<int[]> pairs = lfm.getPairs(finalData);

        // for (int i = 0; i < finalData.length; i++)
        // {
        //     for(int j = 0; j< finalData[0].length; j++)
        //         System.out.print(finalData[i][j] + " ");
        //     System.out.println();
        // }

        /* get teacher student pairs */
        // int numPairs = pairs.size();
        // for(int i = 0; i < numPairs; i++)
        // {
        //     int[] cur = pairs.get(i);
        //     System.out.println(cur[0] + " " + cur[1]);
        // }

        //=====================================================================
        //===========================PERF DATA=================================
        //=====================================================================
        double[][] finalPerfData = lfm.mergeColumns2(topics, activePerf);
        finalPerfData = lfm.cosineCoef(finalPerfData);


        // for (int i = 0; i < finalPerfData.length; i++)
        // {
        //     for(int j = 0; j< finalPerfData[0].length; j++)
        //         System.out.print(finalPerfData[i][j] + " ");
        //     System.out.println();
        // }        

        // ArrayList<int[]> perf_pairs = lfm.getPairs2(finalPerfData);
        // int numPairs = perf_pairs.size();

        // for(int i = 0; i < numPairs; i++)
        // {
        //     int[] cur = perf_pairs.get(i);
        //     System.out.println(cur[0] + " " + cur[1]);
        // }

        //=====================================================================
        //=========================FORUM DATA=================================
        //=====================================================================
        double[][] finalForumData = lfm.mergeColumns3(topics, activeForum);
        finalForumData = lfm.cosineCoef(finalForumData);


        ArrayList<int[]> forum_pairs = lfm.getPairs3(finalForumData);
        int numPairs = forum_pairs.size();

        for(int i = 0; i < numPairs; i++)
        {
            int[] cur = forum_pairs.get(i);
            System.out.println(cur[0] + " " + cur[1]);
        }

    }
}
