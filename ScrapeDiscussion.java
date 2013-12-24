/** This class will scrape a Coursera Discussion Forum
 */

import java.util.*;
import java.io.*;
import java.net.*;

public class ScrapeDiscussion {


	// Class to keep everything we want
	private class PostThread
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
    		String[] concepts = {"DANGLING NODES & DSICONNECTED GRAPH", "USER-MOVIE INTERACTIONS",
    							 "SHARING IS HARD & CONSENSUS IS HARD","CROWDS",
    							 "NETWORK","LAYERS ON LAYERS",
    							 "MOBILE PENETRATION", "MULTIPLE ACCESS", "0G", "FDMA", "1G", "ATTENUATION",
    							 "2G", "TDMA", "CDMA", "COCKTAIL PARTY ANALOGY", "NEAR-FAR PROBLEM", "SIR", "DPC", 
    							 "DPC COMPUTATION", "NEGATIVE FEEDBACK", "CONVERGENCE", "DISTRIBUTED COMPUTATION", "HANDOFFS",
    							 "CDMA & 3G", "UNLICENSED SPECTRUM", "TRAFFIC ANALOGY", "WIFI STANDARDS", "WIFI DEPLOYMENT", 
    							 "ACCESSING WIFI", "INTERFERENCE", "CONTROLLED VS RANDOM ACCESS", "RANDOM ACCESS PROTOCOLS & ALOHA",
    							 "ALOHA THROUGHPUT", "ALOHA INSCALABILITY", "ALOHA SUCCESSFUL TRANSMISSION", "CSMA BACKOFF", "CSMA VS ALOHA",
    							 "SEARCH ENGINES", "WEBGRAPHS", "IN-DEGREE", "THE RANDOM SURFER", "IMPORTANCE EQUATIONS", "NEW WORD IN THE DICTIONARY",
    							 "PAGERANK EXAMPLE CALCULATION", "ROBUST RANKING", "OUR MOBILE DATA PLANS", "DEMAND FOR DATA", "JOBS' INEQUALITY OF CAPACITY",
    							 "USAGE-BASED PLANS", "COMPARING PRICING SCHEMES", "UTILITY", "DEMAND", "DEMAND CURVE & NET UTILITY", "THE TRAGEDY OF COMMONS",
    							 "FLAT RATE CREATES WASTE & FAVORS HEAVY USERS", "CSMA CARRIER SENSING", "NETFLIX TIMELINE", "VIDEO STREAMING", "NETFLIX RECOMMENDATION SYSTEM",
    							 "NETFLIX PRIZE: LOGISTICS", "RAW AVERAGE", "BASELINE PREDICTOR", "COSINE SIMILARITY", "SIMILARITY VALUES", "LEVERAGING SIMILARITY", 
    							 "NETFLIX PRIZE: THE COMPETITION", "NEIGHBORHOOD PREDICTOR", "SHARING", "ARPANET", "NSFNET", "CIRCUIT SWITCHING", "PACKET SWITCHING",
    							 "DISTRIBUTED HIERARCHY", "ROUTING TRAFFIC", "IP ADDRESS", "PREFIX & HOST IDENTIFIER", "DHCP & NAT", "ROUTING PROTOCOLS", "FORWARDING",
    							 "SHORTEST PATH", "BELLMAN-FORD", "COST UPDATES", "RIP AND MESSAGE PASSING", "DIVIDE AND CONQUER", "LAYERED PROTOCOL STACK", "TRANSPORT & NETWORK LAYERS",
    							 "HEADERS", "PROCESSING LAYERS", "CONTROLLING CONGESTION", "TRAFFIC JAM & BUCKET ANALOGY", "END HOSTS", "CAUTIOUS GROWTH OF WINDOW SIZE",
    							 "SLIDING WINDOW", "INFERRING CONGESTION", "CONGESTION CONTROL VERSIONS", "LOSS-BASED CONGESTION INFERENCE", "DELAY-BASED CONGESTION INFERENCE",
    							 "DISTRIBUTED CONGESTION CONTROL"};

    		String text = "class=\"course-forum-post-text\">";
    		int textIndex = forumElement.indexOf(text) + text.length();
    		forumElement = forumElement.substring(textIndex);

    		int endtextIndex = forumElement.indexOf("</div>");
    		String capText = forumElement.substring(0, endtextIndex).toUpperCase();

    		// int count = 0;
    		// while (capText.charAt(count) != '\0')
    		// {
    		// 	if (capText.charAt(count) >='a' && capText.charAt(count) <= 'z')
    		// 		capText.charAt(count) -= 32;

    		// 	count++; 
    		// }

    		for (int i = 0; i < concepts.length; i++)
    		{
    			if (capText.indexOf(concepts[i]) != -1)
    			{
    				System.out.println("Question Concept: " + concepts[i]);
    				break;
    			}
    		}
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
