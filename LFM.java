/* Latent Factor Model Code */

import java.util.*;
import java.io.*;
import java.net.*;

public class LFM {

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

    public double[][] mergeMatrix(double[][] a, double[][] b)
    {
        int row = a.length;
        int col = a[0].length;
        double[][] out = new double[row][col];
        for(int i = 0; i < row; i++)
            for(int j = 0; j < col; j++)
            {
                out[i][j] = a[i][j] + b[i][j];
            }
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

    public static void main(String[] args) throws IOException 
    {
        LFM lfm = new LFM();
        double[][] forumData   = lfm.getMatrix("forum_matrix.csv");
        double[][] performData = lfm.getMatrix("performancematrix.csv");
        //String[] uids = lfm.getUserIds();

        /* Extracting active data */
        boolean[] activeUids = lfm.getActiveRows(performData, 0.0);
        double[][] activePerf = lfm.removeInactiveRows(performData, activeUids);
        double[][] activeForum = lfm.removeInactiveRows(forumData, activeUids);
        
        double[][] finalData   = lfm.mergeMatrix(activeForum, activePerf);
        for (int i = 0; i < finalData.length; i++)
        {
            for(int j = 0; j< finalData[0].length; j++)
            {
                System.out.print(finalData[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Number of rows: " + finalData.length);

    }
}
