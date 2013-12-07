/** This class will scrape Coursera Discussion Forums
 */

import java.util.*;
import java.io.*;
import java.net.*;

public class ScrapeCoursera {

	private PrintWriter writer; // this is the writer that writes to the file

    public void readURLFromString(String url, String filename, String question) throws IOException {

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
        String page = outputPage.toString();
        extractquestions(page, 0, question);
    }

    private void extractquestions(String page, int index, String question)
    {
        String start = "https://class.coursera.org/ni-001/forum/thread?thread_id";
        String end = "&nbsp";

        int startIndex = page.indexOf(start);
        int endIndex = page.indexOf(end);

        while (startIndex != -1 && endIndex != -1)
        {
            String questionElement = page.substring(startIndex, endIndex);

            page = page.substring(endIndex + end.length());
            System.out.println(page);
            System.out.println("===================================================");

            startIndex = page.indexOf(start);
            endIndex = page.indexOf(end);
        }
        
    }

    public static void main(String[] args) throws IOException {
        // main function to execute the scraping
        ScrapeCoursera sc = new ScrapeCoursera();
        String startingUrl = "discuss_data.txt";
        String category = "coursera.txt";
        String question = "question.txt";
        sc.readURLFromString(startingUrl, category, question);
    }
}
