/** This class will scrape Coursera Discussion Forums
 */

import java.util.*;
import java.io.*;
import java.net.*;

public class ScrapeCoursera {

    private class QuestionThread
    {
        String title;
        String asker;
        String lastResponder;
        String time;
        int numViews;
        int numPosts;
    }

	private PrintWriter writer; // this is the writer that writes to the file

    // public void readURLFromString(String url, String filename, String question) throws IOException {

    // 	writer = new PrintWriter(filename);

    //     URL uri = new URL(url);
    //     BufferedReader in = new BufferedReader(new InputStreamReader(uri.openStream()));

    //     StringBuilder outputPage = new StringBuilder();
    //     String inputLine;
        
    //     while ((inputLine = in.readLine()) != null) {
    //         outputPage.append(inputLine);
    //     }
    //     in.close();

    //     writer.print(outputPage.toString());
    //     String page = outputPage.toString();
    //     extractquestions(page, 0, question);
    // }

    public void readFileContent(String filename, String question) throws IOException 
    {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        writer = new PrintWriter(question);

        StringBuilder outputPage = new StringBuilder();
        String inputLine;
        
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

    private void extractQuestionThread(String q)
    {
        String span = "<span>";
        String endSpan = "</span>";
        String ahref = "<a href";
        String endA = "</a>";
        String lpby = "Last post by";
        String title = "title=";
        String important = "important\"><span>";
        String totalNum = "forum-stat\"><span>";
        String startBy = "Started by <span>";
        QuestionThread qt = new QuestionThread();
        StringTokenizer st;

        // Extracting thread title
        int startIndex = q.indexOf(span);
        int endIndex = q.indexOf(endSpan);
        qt.title = q.substring(startIndex + span.length(), endIndex);

        q = q.substring(endIndex + endSpan.length());

        System.out.println("Question Title: " + qt.title);

        char[] array = q.toCharArray();
        // Extracting the thread author
        if (array[q.indexOf("Started") + 17] == 'A')
        	qt.asker = "Anonymous";
        else
        {
	        startIndex = q.indexOf(ahref);
	        q = q.substring(startIndex);
	        startIndex = 0;
	        endIndex = q.indexOf(endA);
	        st = new StringTokenizer(q.substring(0, endIndex), ">");
	        st.nextToken();
	        qt.asker = st.nextToken();
		}

        System.out.println("Author: " + qt.asker);


        // Extract last responder
        startIndex = q.indexOf(lpby);
        q = q.substring(startIndex);
        startIndex = 0;
        endIndex = q.indexOf(endA);
        st = new StringTokenizer(q.substring(0, endIndex), ">");
        st.nextToken();
        qt.lastResponder = st.nextToken();

        System.out.println("Last Responded By: " + qt.lastResponder);


        // Extract time
        startIndex = q.indexOf(title);
        q = q.substring(startIndex + title.length() + 1);
        startIndex = 0;
        endIndex = q.indexOf("\">");
        qt.time = q.substring(startIndex, endIndex);
        System.out.println("Time: " + qt.time);


        // Extract number of posts
        startIndex = q.indexOf(important);
        q = q.substring(startIndex + important.length());
        startIndex = 0;
        endIndex = q.indexOf("</");
        qt.numPosts = Integer.parseInt(q.substring(startIndex, endIndex));
        System.out.println("Number of posts: " + qt.numPosts);

        // Extract number of views
        startIndex = q.indexOf(totalNum);
        q = q.substring(startIndex + totalNum.length());
        startIndex = 0;
        endIndex = q.indexOf("</");
        qt.numViews = Integer.parseInt(q.substring(startIndex, endIndex));
        System.out.println("Number of views: " + qt.numViews);

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

            //System.out.println(questionElement);
            System.out.println("==================================================");
            extractQuestionThread(questionElement);


            page = page.substring(endIndex);

            startIndex = page.indexOf(start);
            page = page.substring(startIndex + start.length());             
            startIndex = 0;

            endIndex = page.indexOf(end);
            int possible = page.indexOf(start);
            if(possible < endIndex && possible >= 0) endIndex = possible;

            if (endIndex < 0)
                endIndex = page.indexOf(special);
        }
        
    }

    public static void main(String[] args) throws IOException {
        // main function to execute the scraping
        ScrapeCoursera sc = new ScrapeCoursera();
        String htmlData = "discuss_data2.txt";
        String category = "coursera.txt";
        String question = "question.txt";
        sc.readFileContent(htmlData, question);
    }
}
