from kafka import KafkaProducer, KafkaConsumer
from random import randint
from uuid import uuid4


class Bidder:
    def __init__(self, bids_topic, result_topic, start_topic):
        super().__init__()
        self.bids_topic = bids_topic
        self.result_topic = result_topic
        self.start_topic = start_topic
        self.group_id = str(uuid4())

        self.start_consumer = KafkaConsumer(
            self.start_topic,
            auto_offset_reset="earliest",
            consumer_timeout_ms=6_000,
            group_id=self.group_id
        )

        # producatorul pentru oferte de licitatie
        self.bid_producer = KafkaProducer()

        # consumatorul pentru rezultatul licitatiei
        self.result_consumer = KafkaConsumer(
            self.result_topic,
            auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
            consumer_timeout_ms=40_000
        )
        self.to_bid = True

        # self.my_bid = randint(1000, 10_000)  # se genereaza oferta ca numar aleator intre 1000 si 10.000
        self.my_id = uuid4()  # se genereaza un identificator unic pentru ofertant

    def bid(self, msg):
        start_value = 0
        auction_id = ''

        for header in msg.headers:
            if header[0] == "id":
                auction_id = str(header[1], encoding="utf-8")
                auction_type = auction_id[auction_id.rfind('#') + 1:]
                #print(auction_type)
            elif header[0] == "value":
                start_value = int.from_bytes(header[1], 'big')

        if self.to_bid is True:
            # determin daca fac bid
            self.my_bid = randint(0, 25_564)

            if self.my_bid > start_value:
                # se construieste mesajul pentru licitare
                print("Trimit licitatia mea: {}...".format(self.my_bid))
                bid_message = bytearray("licitez", encoding="utf-8")  # corpul contine doar mesajul "licitez"
                bid_headers = [  # antetul mesajului contine identitatea ofertantului si, respectiv, oferta sa
                    ("id", bytes(auction_id, encoding='utf-8')),
                    ("amount", self.my_bid.to_bytes(2, byteorder='big')),
                    ("identity", bytes("Bidder {}".format(self.my_id), encoding="utf-8"))
                ]

                # se trimite licitatia sub forma de mesaj catre Kafka
                self.bid_producer.send(topic=self.bids_topic, value=bid_message, headers=bid_headers)

                # exista o sansa din 2 ca oferta sa fie trimisa de 2 ori pentru a simula duplicatele
                if randint(0, 1) == 1:
                    self.bid_producer.send(topic=self.bids_topic, value=bid_message, headers=bid_headers)

                self.bid_producer.flush()

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

        self.bid_producer.close()
        self.result_consumer.close()

    def run(self):
        for msg in self.start_consumer:
            print("Voi licita...cred")
            self.bid(msg)
            self.start_consumer.close()
            self.start_consumer = KafkaConsumer(
                self.start_topic,
                auto_offset_reset="latest",
                consumer_timeout_ms=6_000,
                group_id=self.group_id
            )

        self.get_winner()


if __name__ == '__main__':
    bidder = Bidder(
        bids_topic="topic_oferte",
        result_topic="topic_rezultat",
        start_topic="topic_start"
    )
    bidder.run()
