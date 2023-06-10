import os
import sys
from random import randint
from uuid import uuid4

from kafka import KafkaConsumer, KafkaProducer


class Auctioneer:
    def __init__(self, bids_topic, notify_message_processor_topic, start_topic, auction_type, start_value=0):
        super().__init__()
        self.bids_topic = bids_topic
        self.start_topic = start_topic
        self.notify_processor_topic = notify_message_processor_topic
        self.auction_type = auction_type
        self.start_value = start_value
        self.auction_id = uuid4().__str__()

        if self.auction_type == 'Dutch':
            self.auction_id += '#NL'
        elif self.auction_type == 'English':
            self.auction_id += '#EN'
        elif self.auction_type == 'SealedFirstBid':
            self.auction_id += '#VI'
        elif self.auction_type == 'SealedSecondBid':
            self.auction_id += '#VII'

        if self.auction_type == 'Dutch' or self.auction_type == 'English':
            self.bids_consumer = KafkaConsumer(
                self.bids_topic,
                auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
                group_id="auctioneers",
                consumer_timeout_ms=4_000  # timeout de 15 secunde
            )
        else:
            # consumatorul pentru ofertele de la licitatie
            self.bids_consumer = KafkaConsumer(
                self.bids_topic,
                auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
                group_id="auctioneers",
                consumer_timeout_ms=15_000  # timeout de 15 secunde
            )

        # producatorul pentru notificarea procesorului de mesaje
        self.notify_processor_producer = KafkaProducer()

    def start_auction(self):
        print(f"Incep licitatie {self.auction_id} de tip {self.auction_type} de la valoarea {self.start_value}...")
        start_message = bytearray("start", encoding="utf-8")  # corpul contine doar mesajul "start"
        start_headers = [
            ("id", bytes(self.auction_id, encoding='utf-8')),
            ("value", self.start_value.to_bytes(2, byteorder='big'))
        ]

        # se trimite mesajul
        self.notify_processor_producer.send(topic=self.start_topic, value=start_message, headers=start_headers)

    def receive_bids(self):
        # se preiau toate ofertele din topicul bids_topic

        if not (self.auction_type == 'Dutch' or self.auction_type == 'English'):
            print("Astept oferte pentru licitatie...")
            for msg in self.bids_consumer:
                for header in msg.headers:
                    if header[0] == "identity":
                        identity = str(header[1], encoding="utf-8")
                    elif header[0] == "amount":
                        bid_amount = int.from_bytes(header[1], 'big')

                print("{} a licitat {}".format(identity, bid_amount))

        else:
            print("Verificam ofertele")
            current_value = 0
            i = 0
            while i < 3:
                for msg in self.bids_consumer:
                    for header in msg.headers:
                        if header[0] == "identity":
                            identity = str(header[1], encoding="utf-8")
                        elif header[0] == "amount":
                            bid_amount = int.from_bytes(header[1], 'big')

                    print("{} a licitat {}".format(identity, bid_amount))
                    if bid_amount > self.start_value and bid_amount > current_value:
                        current_value = bid_amount

                self.bids_consumer.close()

                self.bids_consumer = KafkaConsumer(
                    self.bids_topic,
                    auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
                    group_id="auctioneers",
                    consumer_timeout_ms=4_000
                )

                if self.auction_type == 'Dutch' or self.auction_type == 'English':
                    if current_value == 0 or current_value <= self.start_value:
                        if self.auction_type == 'Dutch' and current_value == 0:
                            self.start_value -= randint(0, self.start_value // 3)
                        i += 1
                    else:
                        self.start_value = current_value
                        i = 0

                print("Reincep licitatia")
                self.start_auction()

        # bids_consumer genereaza exceptia StopIteration atunci cand se atinge timeout-ul de 10 secunde
        # => licitatia se incheie dupa ce timp de 15 secunde nu s-a primit nicio oferta
        self.finish_auction()

    def finish_auction(self):
        print("Licitatia s-a incheiat!")
        self.bids_consumer.close()

        # se notifica MessageProcessor ca poate incepe procesarea mesajelor
        auction_finished_message = bytearray("incheiat", encoding="utf-8")
        auction_headers = [
            ("id", bytes(self.auction_id, encoding='utf-8')),
        ]
        self.notify_processor_producer.send(topic=self.notify_processor_topic, value=auction_finished_message, headers=auction_headers)
        self.notify_processor_producer.flush()
        self.notify_processor_producer.close()

    def run(self):
        self.start_auction()
        self.receive_bids()


if __name__ == '__main__':

    auctioneer = Auctioneer(
        bids_topic="topic_oferte",
        notify_message_processor_topic="topic_notificare_procesor_mesaje",
        start_topic="topic_start",
        auction_type=os.environ['AUC_TYPE'],
        start_value=int(os.environ['START_VALUE'])
    )
    auctioneer.run()
