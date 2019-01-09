import json
import time
import urllib.request
import datetime

from kafka import KafkaProducer

# https://wikimedia.org/api/rest_v1/metrics/pageviews/top/en.wikipedia/all-access/2015/07/01

date = datetime.date.today() - datetime.timedelta(1)
url = "https://wikimedia.org/api/rest_v1/metrics/pageviews/top/en.wikipedia/all-access/{}"

# TODO: use correct kafka server
producer = KafkaProducer(bootstrap_servers="localhost:9092")

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
