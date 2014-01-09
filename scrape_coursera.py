# Based off of https://github.com/lionaneesh/coursera-forum-user-crawler

import yaml
import urllib2
import json
from fuzzywuzzy import fuzz
from fuzzywuzzy import process
from StringIO import StringIO

CLASS_ID         = "ni-001"
ASSIGNMENT_FORUM = 3

def get_page(url, use_cookie_2 = False):
    opener = urllib2.build_opener()
    if use_cookie_2:
        c = '__204u=7091002683-1386434309124; __204r=; user_segment=Prospect; ab-experiments-user=improve_list_my3%2Crecs_params; ab-experiments-session=homepage_banner_size2%2Chomepage_browse_all_placement2%2Chomepage_course_count2%2Chomepage_sort_course_3%2Ccoursepage_compress_language%2Ccoursepage_metadata%2Ccoursepage_flat_buttons2%2Ccoursecatalog_inline_enroll2%2Csigtrack_modal_button%2Csigtrack_cert_shareable%2Csigtrack_defer_requirements; maestro_login="jD1V2pt/bR0Pz8KXJUSNTGUnxDhDKk5E2kumzpPgubX2LT6iTCvW1MU+Ww0R/NGGlAE6Y9E7kcP/9pzAYHsjyw==|mRCAxgo374DL1N6yNRkDOh6Zony+s8InBTugfXb/ovv6BU1QqE4KHViK0w2ntLRfEzcwiD0hXqaDiSzTdfOv7/UYhrjmuOVEonbeHnBpnsjLu+0JMQg7xoEPzWohBT2SbCEniNnGgbe5cw67PSSG/D3LgCBeeypfb+tOUcc6CxOE+qlnCM6S7dh18Ni8qk7QeyDEWpRg8deJa+NK1ASSgWcxL/6FaPo9MMQEqMkXpjR6sQDunzGpAOl4KJv3ft11+v6ACVM3eRZGOpsBBF/ukYjblYoCQSeN4sRNKBzj2qN4niGh57cQelmJa1ODjIVUP8RX4iM0sroYtcqSjOWqUzTFkyjoLYQ2PS8nr+rjGzxES4E4VKVioS1gtEyfoxj3Y8XsRRsmhNhlPTmfl1ZuoN9ZAU6elEqsWlp0MSwQ8kH8h04qusgR7p5/GH5/YcRvkvb3ylDZvbWvE4cj0kOcDe5ueibr5ASWbzu8Rb+vc1g="; maestro_login_flag=1; CAUTH=lT8Tu0AGa6LuMR1ODQf1cUrbxM55707qR27DcDWcT4KfbyMw-szxLJ5h5yWDqLqN0vf8di833qbVbR-HxjORgw.D8tYGRFa22G6mSEVgAGT9g.24ZU1Vj_8MD1MhS8esdrx9yfVCI-UjWPAwZSP188HwuIlSOstD5-bzm8C_fUdrT23ilOIJ1WzTDInZsGgdZLlURxc8oTDjO4vy5RLKuxoWfwqePNUSK5k2rMDXHeQ16acP-Z1go4TmycBytB4qEzPHEdwsCBhtZ85xQSgTwOIilo8gxgyu-mSv9557QxGdzW; recently_viewed=startup%2Cni%2Cfriendsmoneybytes; __utma=158142248.934298598.1386434311.1388006003.1388160741.15; __utmb=158142248.48.10.1388160741; __utmc=158142248; __utmz=158142248.1388160741.15.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)'
    else:
        c = 'csrf_token=UypMmy6oHmyvoNXcwUdr; __204u=7091002683-1386434309124; __204r=; ab-experiments-user=honorcode_to_welcome2; user_segment=Prospect; maestro_login_flag=1; CAUTH=lT8Tu0AGa6LuMR1ODQf1cUrbxM55707qR27DcDWcT4KfbyMw-szxLJ5h5yWDqLqN0vf8di833qbVbR-HxjORgw.D8tYGRFa22G6mSEVgAGT9g.24ZU1Vj_8MD1MhS8esdrx9yfVCI-UjWPAwZSP188HwuIlSOstD5-bzm8C_fUdrT23ilOIJ1WzTDInZsGgdZLlURxc8oTDjO4vy5RLKuxoWfwqePNUSK5k2rMDXHeQ16acP-Z1go4TmycBytB4qEzPHEdwsCBhtZ85xQSgTwOIilo8gxgyu-mSv9557QxGdzW; __utma=158142248.934298598.1386434311.1388006003.1388160741.15; __utmb=158142248.58.10.1388160741; __utmc=158142248; __utmz=158142248.1388160741.15.3.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)'

    opener.addheaders.append(('Cookie', c))
    return opener.open(url)

# For now, we do literal string contains. Fuzzy comparison will be better served later
def if_similar (src, dest):
    return fuzz.partial_ratio(src, dest) > 50

def if_question (line):
    qwords = ["who", "what", "when", "where", "which", "whom", "why", "whose", "how"]
    line = line.lower()
    if "?" not in line:
        for w in qwords:
            if w in line:
                return True
        return False
    return True

def get_threads():
    thread_template = "https://class.coursera.org/" + CLASS_ID + "/api/forum/threads/%d"
    user_template = "https://www.coursera.org/maestro/api/user/profiles?user-ids=%d&callback=some"

    uids = set()
    thread_num = 0
    total_threads = 0

    # figure out total number of threads
    res = get_page("https://class.coursera.org/" + CLASS_ID + "/api/forum/forums/0/threads")
    forum_data = json.loads(res.read())
    total_threads = int(forum_data['total_threads'])
    all_output = open('data.yml', "w")
    java_port = open('java_conv.txt', "w")

    # return variables
    all_text_count = 0
    all_threads = []
    discussions = []

    print "Scanning ", total_threads, " threads."


    while thread_num <= total_threads:
        print thread_num
        page_link = thread_template % (thread_num, )
        thread_num += 1
        try:
            data = get_page(page_link).read()
            j_d = json.loads(data)

            # Only extract JSONs that belong to the assignment forum
            if j_d[u'forum_id'] >= 0: #is ASSIGNMENT_FORUM: 
                discussion = {}
                try:
                    discussion = {
                            'user_id': int(j_d[u'user_id']),
                            'title': str(j_d[u'title']), 
                            'last_updated': int(j_d[u'last_updated_time']), 
                            'views': int(j_d[u'num_views']),
                            'upvotes': int(j_d[u'votes']),
                            'num_posts': int(j_d[u'num_posts']),
                            'posts': {},
                        }
                except UnicodeEncodeError, e:
                    continue
                except KeyError, e:
                    continue
                all_text_count += 1

                all_posts = j_d['posts']
                all_comments = j_d['comments']

                java_port.write("-1\n")
                # collecting all posts for a given discussion
                for post_json in all_posts:
                    try:
                        discussion['posts'][post_json[u'id']] = {
                                'user_id': int(post_json[u'user_id']),
                                'order': int(post_json[u'order']),
                                'upvotes': int(post_json[u'votes']),
                                'text': str(post_json[u'post_text']),
                                'comments': [],
                            }
                        post = discussion['posts'][post_json[u'id']]
                        java_port.write(str(post['user_id']) + "\n")
                        java_port.write(str(post['upvotes']) + "\n")
                        java_port.write(post['text'] + "\n")
                        java_port.write("****\n")
                    except UnicodeEncodeError, e:
                        continue
                    except KeyError, e:
                        continue
                all_text_count += len(all_posts)

                # collecting all comments and placing under the proper post
                for comment_d in all_comments:
                    try:
                        post = discussion['posts'][comment_d[u'post_id']]
                        comment = {
                                'user_id': int(comment_d[u'user_id']),
                                'upvotes': int(comment_d[u'votes']),
                                'text': str(comment_d[u'comment_text'])
                            }

                        # provide necessary data for the java programs
                        # In the format: "user_id\n upvotes\n text\n ****""
                        java_port.write(str(comment['user_id']) + "\n")
                        java_port.write(str(comment['upvotes']) + "\n")
                        java_port.write(comment['text'] + "\n")
                        java_port.write("****\n")
                        
                        post['comments'].append(comment)
                    except UnicodeEncodeError, e:
                        continue
                    except KeyError, e:
                        continue
                all_text_count += len(all_comments)

                all_output.write(yaml.dump(discussion, default_flow_style=False))
                all_output.write('\n')

                if discussion is not {}:
                    discussions.append(discussion)

        except urllib2.HTTPError, error:
            if error.read() == "Unexpected API error":
                there_are_more_threads = False

    # extracting the necessary content for the return values
    # index = 0
    # all_threads = []
    # for discussion in discussions:
    #     all_threads.append([discussion['user_id'], discussion['upvotes'], discussion['title']])
    #     for post_id in discussion['posts']:
    #         post = discussion['posts'][post_id]
    #         all_threads.append([post['user_id'], post['upvotes'], post['text']])
    #         for comment in post['comments']:
    #             all_threads.append([comment['user_id'], comment['upvotes'], comment['text']])
    # return all_threads
    return discussions

def get_uids():
    uidCSV = open('userIDs.csv', 'r')
    index = 0
    uidsToIndex = {}

    for line in uidCSV:
        uids = line.split(',')
        if uids[0] is not '':
            uidsToIndex[int(uids[0])] = index
            index = index + 1
    return uidsToIndex

def get_concepts(filename):
    input = open(filename, 'r')
    lines = input.readlines()
    concepts = []
    for line in lines:
        concepts.append((" ".join(line.split()[1:])).upper())

    input.close()
    return concepts

def setupForumMatrix(discussions, uidsToRow):
    concepts = get_concepts('fName_to_QuizName.txt')
    index = 0
    UID = 0
    VOTES = 1
    TEXT = 2

    num_concepts = len(concepts)
    scores = [[0] * num_concepts for i in range(len(uidsToRow))]
    conceptToCol = {}

    for concept in concepts: 
        conceptToCol[concept] = index
        index = index + 1

    # for post in posts:
    #     for concept in concepts:
    #         if concept in post[TEXT].upper() and post[UID] is not 0:
    #             if "?" in post[TEXT]:
    #                 scores[uidsToRow[post[UID]]] [conceptToCol[concept]] = 0
    #             elif post[VOTES] > 0:
    #                 scores[uidsToRow[post[UID]]] [conceptToCol[concept]] = 2

    for d in discussions:
        title = d['title']
        for concept in concepts:

            # If this particular discussion is relevant to a particular concept
            if if_similar(concept, title):

                if d['user_id'] > 0:
                    scores [uidsToRow[d['user_id']]] [conceptToCol[concept]] = -1

                posts = d['posts']
                for post_id in posts:
                    uid = posts[post_id]['user_id']
                    if uid > 0:
                        # A question is asked about this particular concept discussion
                        if if_question(posts[post_id]['text']):
                        # if "?" in posts[post_id]['text']:
                            scores [uidsToRow[uid]] [conceptToCol[concept]] = -1
                        # Not a question, and has upvotes, shows proficiency
                        elif posts[post_id]['upvotes'] > 0:
                            scores [uidsToRow[uid]] [conceptToCol[concept]] = 1

                    comments = posts[post_id]['comments']
                    for comment in comments:
                        uid = comment['user_id']
                        if uid > 0:
                            if if_question(comment['text']):
                            # if "?" in comment['text']:
                                scores [uidsToRow[uid]] [conceptToCol[concept]] = -1
                            elif comment['upvotes'] > 0:
                                scores [uidsToRow[uid]] [conceptToCol[concept]] = 1
                break

    return scores

def printMatrix(matrix):
    output = open("forum_matrix2.csv", "w")
    output.write(str(len(matrix)) + '\n')
    output.write(str(len(matrix[0])) + '\n')
    for row in matrix:
        for elem in row:
            output.write(str(int(elem))+",")
        output.write('\n')
    output.close()

get_uids()
printMatrix(setupForumMatrix(get_threads() ,get_uids()))