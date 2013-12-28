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

    private void parsePosts(String dataFile) throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(dataFile));
        while (in.ready())
        {
            extractPostInfo(in);
        }
    }

    // Extract relevant information from each post in a discussion forum
    private void extractPostInfo(BufferedReader in) throws IOException
    {
        // Extract the user id as well as the upvotes
        int uid = Integer.parseInt(in.readLine());
        boolean isQuestion = false;

        // check to see if there is a -1 in the front, if so, this is a new thread
        // if not, continue as normally.
        if (uid < 0)
        {
            isQuestion = true;
            uid = Integer.parseInt(in.readLine());
        }

        int upvotes = Integer.parseInt(in.readLine());
        String text = "";
        String line = in.readLine();
        while (!line.equals("****"))
        {
            text += line + " ";
            line = in.readLine();
        }
        text = text.toUpperCase();

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

            for (int i = 0; i < concepts.length; i++)
            {
                if (text.indexOf(concepts[i]) != -1)
                {
                    System.out.println("Question Concept: " + concepts[i]);
                    break;
                }
            }
        }

        // No point checking for upvotes in question - so else
        else
        {
            // If yes, add to teacher array
            if (upvotes > 0 && uid != 0)
                System.out.println("Teacher: " + uid);

            // Check for students only if not teachers - i.e. ignore questions marks in posts with upvotes
            else            
            {
                // Go through the text of the post
                if (text.indexOf("?") != -1 && uid != 0)
                    System.out.println("Student for supplementary post: " + uid);
            }
        }
    }

    // main function to execute the scraping
    public static void main(String[] args) throws IOException {
        ScrapeDiscussion sc = new ScrapeDiscussion();

        String htmlData = "lecturedis1.txt";
        String dataFile = "java_conv.txt";

//        sc.readFileContent(htmlData);
        sc.parsePosts(dataFile);
    }
}
