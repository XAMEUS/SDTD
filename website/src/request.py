import json
import sys
from kafka import KafkaConsumer,KafkaProducer

kafka_servers = ['kafka-0.kafka-svc:9093']
TOPIC_QUERIES = "requests"
TOPIC_RESULTS = "results"

HELP = """
    date: YYYY-MM-DD
    <0> <from date> <to date> <article>
    <1> <from date> <to date> <number of top>
    <2>  TODO
"""

def main(argv):
    request = None
    if int(argv[1]) == 0:
        request = {"type":0, "from":argv[2], "to":argv[3], "article":argv[4]}
    if int(argv[1]) == 1:
        request = {"type":1, "from":argv[2], "to":argv[3], "tops":int(argv[4])}
    if int(argv[1]) == 2:
        request = {"todo": "todo"}
    data = json.dumps({"request": request}).encode()
    print(data)

    consumer = KafkaConsumer(TOPIC_RESULTS, bootstrap_servers=kafka_servers)
    producer = KafkaProducer(bootstrap_servers=kafka_servers)

    producer.send(TOPIC_QUERIES, data)
    for message in consumer:
        msg = json.loads(message.value.decode())
        if msg['request'] == request and 'result' in msg:
            print(msg['result'])
            break

if __name__ == "__main__":
    if len(sys.argv) <= 1:
        print(HELP)
    else:
        main(sys.argv)
