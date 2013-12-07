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

    public void readFileContent(String filename, String question) throws IOException 
    {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        writer = new PrintWriter(question);

        StringBuilder outputPage = new StringBuilder();
        String inputLine;
        
        //while ((inputLine = in.readLine()) != null) {
        while (in.ready())
        {
            inputLine = in.readLine();
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
        String end = "nbsp";
        String special = "</i> See top forum posters</a></div></div>";

        int startIndex = page.indexOf(start);
        page = page.substring(startIndex);

        startIndex = 0;
        int endIndex = page.indexOf(end);

        while (startIndex >= 0 && endIndex >= 0)
        {
            String questionElement = page.substring(startIndex, endIndex);

            page = page.substring(endIndex + end.length());

            startIndex = page.indexOf(start);
            endIndex = page.indexOf(end);
            if (endIndex < 0)
                endIndex = page.indexOf(special);
        }
        
    }

    public static void main(String[] args) throws IOException {
        // main function to execute the scraping
        ScrapeCoursera sc = new ScrapeCoursera();
        String startingUrl = "discuss_data.txt";
        String htmlData = "discuss_data.txt";
        String category = "coursera.txt";
        String question = "question.txt";
        sc.readFileContent(htmlData, question);
    }
}
