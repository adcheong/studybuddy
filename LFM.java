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
    
    public static void main(String[] args) throws IOException 
    {
        LFM lfm = new LFM();
        double[][] forumData   = lfm.getMatrix("forum_matrix.csv");
        double[][] performData = lfm.getMatrix("performancematrix.csv");

        double[][] finalData   = lfm.mergeMatrix(forumData, performData);
        System.out.println(finalData[1101][10]);
    }
}
