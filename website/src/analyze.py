import json
import time
import urllib.request
import datetime
import sys
import logging
logging.basicConfig(level=logging.DEBUG)

from kafka import KafkaProducer

# https://wikimedia.org/api/rest_v1/metrics/pageviews/top/en.wikipedia/all-access/2015/07/01

def download_and_publish(date):
    #date = datetime.date.today() - datetime.timedelta(1)
    url = "https://wikimedia.org/api/rest_v1/metrics/pageviews/top/en.wikipedia/all-access/{}"

    # TODO: use correct kafka server
    producer = KafkaProducer(bootstrap_servers="kafka-0.kafka-svc:9093")

    while True:
        print("REQUEST", url.format(date.isoformat().replace('-', '/')))
        response = urllib.request.urlopen(url.format(date.isoformat().replace('-', '/')))
        data = json.loads(response.read().decode())
        for article in data['items'][0]['articles']:
            article['date'] = date.isoformat()
            # print(article)
            # print(json.dumps(article).encode())
            producer.send("articles", json.dumps(article).encode())
        print("{} Produced {} article records".format(time.time(), len(data['items'][0]['articles'])))
        time.sleep(1)
        date -= datetime.timedelta(1)
        # break

if __name__ == "__main__":
    if len(sys.argv) == 2:
        download_and_publish(sys.argv[1])
