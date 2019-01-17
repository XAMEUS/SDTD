import json
from kafka import KafkaConsumer,KafkaProducer

# pour tester request.py
# fake kafka spark responses

kafka_servers = ['kafka-0.kafka-svc:9093']
TOPIC_QUERIES = "requests"
TOPIC_RESULTS = "results"

consumer = KafkaConsumer(TOPIC_QUERIES, bootstrap_servers=kafka_servers)
producer = KafkaProducer(bootstrap_servers=kafka_servers)
for message in consumer:
    msg = json.loads(message.value.decode())
    print(msg)
    msg['result'] = 'todo'
    producer.send(TOPIC_RESULTS, json.dumps(msg).encode())
