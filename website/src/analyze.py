import json
import time
import urllib.request
import datetime
import sys
import logging
logging.basicConfig(level=logging.DEBUG)

from kafka import KafkaProducer

# Run this program
# python3 analyze.py 2016-12-01
# python3 analyze.py 2016-12-01 2016-12-31
# python3 analyze.py 2018-01-01 2018-12-31


# Example call of the API
#          https://wikimedia.org/api/rest_v1/metrics/pageviews/top/en.wikipedia/all-access/2015/07/01
API_URL = "https://wikimedia.org/api/rest_v1/metrics/pageviews/top/en.wikipedia/all-access/{}"

def download_and_publish(str_date_begin, str_date_end=None):
    #date = datetime.date.today() - datetime.timedelta(1)

    start_date = str2date(str_date_begin)

    if str_date_end is None:
        date = start_date
    else:
        date = str2date(str_date_end)

    # TODO: use correct kafka server
    producer = KafkaProducer(bootstrap_servers="kafka-0.kafka-svc:9093")

    while date >= start_date:
        str_day = date2str(date)
        try:
            print("REQUEST", API_URL.format(str_day.replace('-', '/')))
            response = urllib.request.urlopen(API_URL.format(str_day.replace('-', '/')))
            data = json.loads(response.read().decode())
            for article in data['items'][0]['articles']:
                article['date'] = str_day
                # print(article)
                # print(json.dumps(article).encode())
                producer.send("articles", json.dumps(article).encode())
                print("{} Produced {} article records".format(time.time(), len(data['items'][0]['articles'])))
        except Exception as e:
            print("Unable to load data for day: {}".format(str_day))
            print(e)

        time.sleep(1)
        date -= datetime.timedelta(1) # 1 day
        # break

def str2date(stringDate):
    return datetime.datetime.strptime(stringDate, "%Y-%m-%d")

def date2str(date):
    return date.isoformat()[:10]

if __name__ == "__main__":
    if len(sys.argv) == 1:
        print("Correct usage:")
        print("{} <start_date> [end_date]".format(sys.argv[0]))
        print("<date> follows isoformat (ex: 2018-01-30)")
        print("(start_date & end_date included)")
        print("(start_date < end_date)")
        exit(1)

    if len(sys.argv) == 2:
        download_and_publish(sys.argv[1])
    elif len(sys.argv) == 3:
        download_and_publish(sys.argv[1], sys.argv[2])
