from datetime import datetime
from random import randrange
from kafka import KafkaConsumer, KafkaProducer
from pynput.mouse import Controller
import time


class MessageProcessor:
    def __init__(self, mouse_topic, random_topic):
        super().__init__()
        self.mouse_topic = mouse_topic
        self.random_topic = random_topic

        # producatorul pentru mesajele procesate
        self.producer = KafkaProducer()

    def get_mouse_pos(self):
        mouse = Controller()
        lista = []
        for _ in range(100):
            pos = mouse.position
            print(pos)
            lista.append(pos)
            time.sleep(0.1)
        return lista

    def generate_random_numbers(self):
       lista = [(randrange(0,1920),randrange(0,1080)) for x in range(0,100)]
       return lista

    def send_to_kafka(self, lista, topic):
        for x in lista:
            bitisori = bytearray(str(x), 'utf-8')
            print(bitisori)
            self.producer.send(topic=topic, value=bitisori, headers=[])
            time.sleep(0.1)
        # self.processed_bids_producer.send(topic=self.processed_bids_topic, value=bid.value, headers=bid.headers)

    def run(self):
        l = self.get_mouse_pos()
        self.send_to_kafka(lista=l, topic=self.mouse_topic)
        l = self.generate_random_numbers()
        self.send_to_kafka(lista=l, topic=self.random_topic)


if __name__ == '__main__':
    generator = MessageProcessor(
        mouse_topic="topic_mouse",
        random_topic="topic_random",
    )
    generator.run()
