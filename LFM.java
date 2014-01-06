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

    // public String[] getUserIds() throws IOException
    // {
    //     ArrayList<String> list = new ArrayList<String>();
    //     BufferedReader in = new BufferedReader(new FileReader("userIDs.csv"));

    //     while(in.ready())
    //     {
    //         String line = in.readLine();
    //         if (line.charAt(0) == ',')
    //             break;
    //         StringTokenizer st = new StringTokenizer(line, ",");
    //         list.add(st.nextToken());
    //     }

    //     String[] output = new String[list.size()];
    //     output = list.toArray(output);
    //     return output;
    // }

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

    public static void main(String[] args) throws IOException 
    {
        LFM lfm = new LFM();

        /* Basic I/O */
        lfm.getColumnMapping("fName_to_QuizName.txt");
        double[][] forumData   = lfm.getMatrix("forum_matrix.csv");
        double[][] performData = lfm.getMatrix("performancematrix.csv");
        //String[] uids = lfm.getUserIds();

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

        /* Merging the active data so the columns will represent each topic */
        topic[] topics = lfm.getTopics("topics.txt");
        double[][] finalData = lfm.mergeColumns(topics, activeData);

        for (int i = 0; i < finalData.length; i++)
        {
            for(int j = 0; j< finalData[0].length; j++)
                System.out.print(finalData[i][j] + " ");
            System.out.println();
        }

    }
}
