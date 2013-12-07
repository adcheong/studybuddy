/** This class will scrape Coursera Discussion Forums
 */

import java.util.*;
import java.io.*;
import java.net.*;

public class ScrapeCoursera {

	private PrintWriter writer; // this is the writer that writes to the file

    public void readURLFromString(String url, String filename) throws IOException {

    	writer = new PrintWriter(filename);

        URL uri = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(uri.openStream()));

        StringBuilder outputPage = new StringBuilder();
        String inputLine;
        
        while ((inputLine = in.readLine()) != null) {
            outputPage.append(inputLine);
        }
        in.close();

        writer.print(outputPage.toString());
    }

    public static void main(String[] args) {
        // main function to execute the scraping
        ScrapeCoursera sc = new ScrapeCoursera();
        String startingUrl = "http://en.wikipedia.org/wiki/Category:Articles";
        String category = "coursera.txt";
        sc.readURLFromString(startingUrl, category);
    }
}
