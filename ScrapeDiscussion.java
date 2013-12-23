/** This class will scrape a Coursera Discussion Forum
 */

import java.util.*;
import java.io.*;
import java.net.*;

public class ScrapeDiscussion {

    // Read contents from text file -- same as ScrapeCoursera
    public void readFileContent(String filename) throws IOException 
    {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        StringBuilder outputPage = new StringBuilder();
        String inputLine;
        
        while (in.ready())
        {
            inputLine = in.readLine();
            outputPage.append(inputLine);
        }

        in.close();

        String page = outputPage.toString();
        extractposts(page);
    }

    // Extract relevant things from each discussion forum
    private void extractposts(String page)
    {
        String start = "<div class=\"course-forum-post-container\">";
        String end = "<div class=\"course-forum-comments-container\">";

        int startIndex = page.indexOf(start);

        page = page.substring(startIndex);

        startIndex = 0;
        int endIndex = page.indexOf(end);

        // Change of end as question post is different from all other subsequent posts
        end = "<div class=\"course-forum-new-comment-link-container\">";

        while (startIndex >= 0 && endIndex >= 0)
        {
            String forumElement = page.substring(startIndex, endIndex);

            //System.out.println(forumElement);
            System.out.println("==================================================");
            extractPostInfo(questionElement);
            
            page = page.substring(endIndex);

            startIndex = page.indexOf(start);
            page = page.substring(startIndex + start.length());             
            startIndex = 0;

            endIndex = page.indexOf(end);
        }
    }

    // Extract relevant information from each post in a discussion forum

    // main function to execute the scraping
    public static void main(String[] args) throws IOException {

        ScrapeDiscussion sc = new ScrapeDiscussion();

        String htmlData = "discussionforum.txt";

        sc.readFileContent(htmlData);
    }
}
