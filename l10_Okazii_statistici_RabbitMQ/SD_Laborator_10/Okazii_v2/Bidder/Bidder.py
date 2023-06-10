from kafka import KafkaProducer, KafkaConsumer
from random import randint
from uuid import uuid4
import mq_communication as mq
import datetime

class Bidder:
    def __init__(self, bids_topic, result_topic):
        super().__init__()
        self.rabbit = mq.RabbitMq()
        self.bids_topic = bids_topic
        self.result_topic = result_topic

        # producatorul pentru oferte de licitatie
        self.bid_producer = KafkaProducer()

        # consumatorul pentru rezultatul licitatiei
        self.result_consumer = KafkaConsumer(
            self.result_topic,
            auto_offset_reset="earliest"  # mesajele se preiau de la cel mai vechi la cel mai recent
        )

        self.my_bid = randint(1000, 10_000)  # se genereaza oferta ca numar aleator intre 1000 si 10.000
        self.my_id = uuid4()  # se genereaza un identificator unic pentru ofertant
        self.notify_monitor_producer = KafkaProducer()

    def sendToMonitor(self,msg):
        new_msg = f'Bidder {self.my_id}~' + str(datetime.datetime.now())  + '~' + str(msg, encoding='utf-8')
        self.rabbit.send_message(message=new_msg)

    def bid(self):
        # se construieste mesajul pentru licitare
        print("Trimit licitatia mea: {}...".format(self.my_bid))
        bid_message = bytearray("licitez", encoding="utf-8")  # corpul contine doar mesajul "licitez"
        bid_headers = [  # antetul mesajului contine identitatea ofertantului si, respectiv, oferta sa
            ("amount", self.my_bid.to_bytes(2, byteorder='big')),
            ("identity", bytes("Bidder {}".format(self.my_id), encoding="utf-8"))
        ]

        # se trimite licitatia sub forma de mesaj catre Kafka
        self.bid_producer.send(topic=self.bids_topic, value=bid_message, headers=bid_headers)
        self.sendToMonitor(bid_message)

        # exista o sansa din 2 ca oferta sa fie trimisa de 2 ori pentru a simula duplicatele
        if randint(0, 1) == 1:
            self.bid_producer.send(topic=self.bids_topic, value=bid_message, headers=bid_headers)
            self.sendToMonitor(bytes(f'{bid_message}{self.my_bid}', encoding='utf-8'))

        self.bid_producer.flush()
        self.bid_producer.close()

    def get_winner(self):
        # se asteapta raspunsul licitatiei
        print("Astept rezultatul licitatiei...")
        result = next(self.result_consumer)

        # se verifica identitatea castigatorului
        for header in result.headers:
            if header[0] == "identity":
                identity = str(header[1], encoding="utf-8")

        if identity == "Bidder {}".format(self.my_id):
            print("[{}] Am castigat!!!".format(self.my_id))
        else:
            print("[{}] Am pierdut...".format(self.my_id))

        self.result_consumer.close()

    def run(self):
        self.bid()
        self.get_winner()


if __name__ == '__main__':
    bidder = Bidder(
        bids_topic="topic_oferte",
        result_topic="topic_rezultat"
    )
    bidder.run()
