studybuddy
==========
README

Below explains each major program's utility and purpose. 

scrape_coursera.py : scrape textual content from the coursera course. 
Execution: python scrape_coursera.py

    Input:

    Python Library: install fuzzywuzzy
    Course ID: Set CLASS_ID at top of scrape_coursera.py for a specific course.
    User IDs: list of user id pairs in a file called userIDs.csv
    Concepts List: fName_to_QuizName.txt contains the list of concepts needed

    Output:

    data.yml: contains all data in yaml format for all threads in the course.
    java_conv.txt: text file that will be used in LFM to extract forum data.
    forum_matrix.csv: the matrix with all the forum data where rows are users and concepts are columns

ScrapePerformance.java : extracts the performance data of a coursera course
Execution:  java ScrapePerformance.java
            java ScrapePerformance

    Input:

    Several text files with users and their quiz performance data

    Output

    performancematrix.csv: csv file with all the recorded data of the performance scores.

LFM.java : analyzes the data from two csv files
Execution:  java LFM.java
            java LFM

    Input:

    fName_to_QuizName.txt: text file with names of quiz concepts
    forum_matrix.csv: csv containing qualitative data from coursera course
    performancematrix.csv: csv containing the quiz performance data
    topics.txt: list of quiz concepts associated to a particular lesson

    Output (standard output):

    List of pairs of user ids that form a valid teacher-student pairing
