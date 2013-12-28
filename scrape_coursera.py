# Based off of https://github.com/lionaneesh/coursera-forum-user-crawler

import yaml
import urllib2
import json
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

    print "Scanning ", total_threads, " threads."


    while thread_num <= total_threads:
        print thread_num
        page_link = thread_template % (thread_num, )
        thread_num += 1
        try:
            data = get_page(page_link).read()
            j_d = json.loads(data)

            # Only extract JSONs that belong to the assignment forum
            if j_d[u'forum_id'] is ASSIGNMENT_FORUM: 
                forum = {
                        'user_id': int(j_d[u'user_id']),
                        'title': str(j_d[u'title']), 
                        'last_updated': int(j_d[u'last_updated_time']), 
                        'views': int(j_d[u'num_views']),
                        'upvotes': int(j_d[u'votes']),
                        'num_posts': int(j_d[u'num_posts']),
                        'posts': {},
                    }
                all_posts = j_d['posts']
                all_comments = j_d['comments']

                # collecting all posts for a given forum
                for post_json in all_posts:
                    forum['posts'][post_json[u'id']] = {
                            'user_id': int(post_json[u'user_id']),
                            'order': int(post_json[u'order']),
                            'upvotes': int(post_json[u'votes']),
                            'text': str(post_json[u'post_text']),
                            'comments': [],
                        }

                # collecting all comments and placing under the proper post
                for comment_d in all_comments:
                    post = forum['posts'][comment_d[u'post_id']]
                    comment = {
                            'user_id': int(comment_d[u'user_id']),
                            'upvotes': int(comment_d[u'votes']),
                            'text': str(comment_d[u'comment_text'])
                        }
                    post['comments'].append(comment)
                all_output.write(yaml.dump(forum, default_flow_style=False))
                all_output.write('\n')

                # uids.add(j_d[u'user_id'])
                # posts_and_comments = j_d['posts'] + j_d['comments']
                # for entity in posts_and_comments:
                #     try:
                #         uids.add(entity[u'user_id'])
                #     except KeyError:
                #         pass
        except urllib2.HTTPError, error:
            if error.read() == "Unexpected API error":
                there_are_more_threads = False

    # now we have all the uids let's get the user data
    users = {}
    number_of_users = len(uids)
    count = 1

    print "Crawling %d user profiles." % (number_of_users,)
    temp_count = 0
    for uid in uids:
        data = get_page(user_template % (uid,), True).read()
        # data = data[len(CALLBACK) + 2 : len(data) - 2].strip()
        if data:
            user = json.loads(data)
            users[uid] = user[u'display_name']
        count += 1
        temp_count += 1
    print users
get_threads()