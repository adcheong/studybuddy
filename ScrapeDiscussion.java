/** This class will scrape a Coursera Discussion Forum
 */

import java.util.*;
import java.io.*;
import java.net.*;

public class ScrapeDiscussion {

	private class QuestionThread
    {
        String qTopic;
        int[] teacher;
        int[] student;
    }

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
        String start1 = "<div class=\"course-forum-post-top-container\">";
        String start2 = "class=\"course-forum-post-view-container \">";

        end = "<div id=\"course-forum-post-vote-hint-";
        boolean isQuestion = true;

        while (startIndex >= 0 && endIndex >= 0)
        {
            String forumElement = page.substring(startIndex, endIndex);

            //System.out.println(forumElement);
            //System.out.println("==================================================");
            extractPostInfo(forumElement, isQuestion);
            
            isQuestion = false;
            page = page.substring(endIndex);

            int start1Index = page.indexOf(start1);
            if (start1Index == -1)
            	start1Index = Integer.MAX_VALUE;
            int start2Index = page.indexOf(start2);
            if (start2Index == -1)
            	start2Index = Integer.MAX_VALUE;

            if (start1Index == Integer.MAX_VALUE && start2Index == Integer.MAX_VALUE)
            {
            	startIndex = -1;
            	break;
            }
            else if (start1Index < start2Index)
            	startIndex = start1Index;
            else
            	startIndex = start2Index;

            // startIndex = page.indexOf(start);
            page = page.substring(startIndex + (startIndex == start1Index ? start1.length() : start2.length()));             
            startIndex = 0;

            endIndex = page.indexOf(end);
        }
    }

    // Extract relevant information from each post in a discussion forum
    private void extractPostInfo(String forumElement, boolean isQuestion)
    {
    	// To figure out topic/concept in question
    	if (isQuestion)
    	{
    		// Figure out topic/concept in question
    	}

    	// No point checking for upvotes in question - so else
    	else
    	{
    		// Check if upvotes exist
    		String upvote = "<span class=\"course-forum-post-vote-count course-forum-votes-positive\">";
    		int upIndex = forumElement.indexOf(upvote);

    		// If yes, add to teacher array
			if (upIndex != -1)
			{
				String name = "<a href=\"https://class.coursera.org/ni-001/forum/profile?user_id=";
    			int nameIndex = forumElement.indexOf(name) + name.length();
    			forumElement = forumElement.substring(nameIndex);

    			int endNameIndex = forumElement.indexOf("\"");
    			System.out.println(forumElement.substring(0, endNameIndex));
			}    		

    	// sentiment analysis for student
    	// if (true)
    	// add to student array
    	}
	}

    // main function to execute the scraping
    public static void main(String[] args) throws IOException {

        ScrapeDiscussion sc = new ScrapeDiscussion();

        String htmlData = "discussionforum.txt";

        sc.readFileContent(htmlData);
    }
}
