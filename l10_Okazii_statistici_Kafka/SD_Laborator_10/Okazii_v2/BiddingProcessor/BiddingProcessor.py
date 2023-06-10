from kafka import KafkaConsumer, KafkaProducer
import datetime

class BiddingProcessor:
    def __init__(self, processed_bids_topic, result_topic, monitor_topic):
        super().__init__()
        self.monitor_topic = monitor_topic
        self.processed_bids_topic = processed_bids_topic
        self.result_topic = result_topic

        # consumatorul pentru ofertele procesate
        self.processed_bids_consumer = KafkaConsumer(
            self.processed_bids_topic,
            auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
            consumer_timeout_ms=3000
        )

        # producatorul pentru trimiterea rezultatului licitatiei
        self.result_producer = KafkaProducer()
        self.notify_monitor_producer = KafkaProducer()

    def get_processed_bids(self):
        # se preiau toate ofertele procesate din topicul processed_bids_topic
        print("Astept ofertele procesate de MessageProcessor...")

        # ofertele se stocheaza sub forma de perechi <PRET_LICITAT, MESAJ_OFERTA>
        bids = dict()
        no_bids_available = True

        while no_bids_available:
            for msg in self.processed_bids_consumer:
                for header in msg.headers:
                    if header[0] == "amount":
                        bid_amount = int.from_bytes(header[1], 'big')
                bids[bid_amount] = msg

            # daca inca nu exista oferte, se asteapta in continuare
            if len(bids) != 0:
                no_bids_available = False


        self.processed_bids_consumer.close()
        self.decide_auction_winner(bids)

    def sendToMonitor(self,msg):
        msg_headers = [
            ("timestamp", bytes(str(datetime.datetime.now()), encoding="utf-8")),
            ("identity", bytes("BiddingProcessor", encoding="utf-8"))
        ]
        self.notify_monitor_producer.send(topic=self.monitor_topic, value=msg, headers=msg_headers)

    def decide_auction_winner(self, bids):
        print("Procesez ofertele...")

        if len(bids) == 0:
            print("Nu exista nicio oferta de procesat.")
            return

        # sortare dupa oferte, descrescator
        sorted_bids = sorted(bids.keys(), reverse=True)

        # castigatorul este ofertantul care a oferit pretul cel mai mare
        winner = bids[sorted_bids[0]]

        for header in winner.headers:
            if header[0] == "identity":
                winner_identity = str(header[1], encoding="utf-8")

        print("Castigatorul este:")
        print("\t{} - pret licitat: {}".format(winner_identity, sorted_bids[0]))

        # se trimite rezultatul licitatiei pentru ca entitatile Bidder sa il preia din topicul corespunzator
        self.result_producer.send(topic=self.result_topic, value=winner.value, headers=winner.headers)
        self.sendToMonitor(bytes(f'{winner_identity} a castigat cu {sorted_bids[0]}', encoding='utf-8'))
        self.result_producer.flush()
        self.result_producer.close()

    def run(self):
        self.get_processed_bids()


if __name__ == '__main__':
    bidding_processor = BiddingProcessor(
        monitor_topic="topic_monitor",
        processed_bids_topic="topic_oferte_procesate",
        result_topic="topic_rezultat"
    )
    bidding_processor.run()
