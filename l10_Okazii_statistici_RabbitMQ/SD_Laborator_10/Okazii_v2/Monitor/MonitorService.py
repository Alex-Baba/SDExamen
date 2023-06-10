from asyncio import gather
from datetime import datetime
from kafka import KafkaConsumer, KafkaProducer
import mq_communication as mq

# s-ar putea replica pentru incarcare mai mare...daca spui asta la examen esti zeu
class Monitor:
    def __init__(self, monitor_topic, log_file):
        super().__init__()
        self.rabbit = mq.RabbitMq(self)
        self.queue_msg = ''
        # dict pentru statistici
        self.stats = {}

        self.monitor_topic = monitor_topic
        self.log_file = log_file
        # consumatorul pentru ofertele de la licitatie
        self.bids_consumer = KafkaConsumer(
            self.monitor_topic,
            auto_offset_reset="earliest",  # mesajele se preiau de la cel mai vechi la cel mai recent
        )

    def set_response(self, result):
        self.queue_msg = result

    def end_of_bid(self):
        filtered = self.stats['total_msg'] - self.stats['msg_proc_msg']
        with open(self.log_file, 'a') as f:
            f.write('----------------------------------------\n')
            f.write(f'Licitatia a inceput la: {self.stats["start_time"]}\n')
            f.write(f'Licitatia s-a incheiat la: {self.stats["stop_time"]}\n')
            f.write(f'Licitatia a durat: {self.stats["duration"]}\n')
            f.write(f'Licitatia a avut: {len(self.stats["bidders"])} bidderi\n')
            f.write(f'Licitatia a filtrat: {filtered} mesaje\n')
            f.write(f'Licitatia a fost castigata de: {self.stats["winner"]} cu {self.stats["winner_amount"]}\n')
            f.write('----------------------------------------\n\n')

    # stats:
    # mesaje_totale, bidders, mesaje_filtrare(respinse), durata, castigator
    def gather_stat(self, msg, timestamp, id):
        print(id,timestamp,msg)

        if 'total_msg' not in self.stats:
            self.stats['total_msg'] = 0
        self.stats['total_msg'] += 1

        if 'start_time' not in self.stats:
            self.stats['start_time'] = timestamp
        if id == 'BiddingProcessor' and 'a castigat' in msg:
            self.stats['stop_time'] = timestamp
            self.stats['duration'] = (datetime.strptime(self.stats['stop_time'], '%Y-%m-%d %H:%M:%S.%f') - datetime.strptime(self.stats['start_time'], '%Y-%m-%d %H:%M:%S.%f')).total_seconds()
            self.stats['winner'] = msg[:msg.find('a castigat')]
            self.stats['winner_amount'] = msg[msg.rfind('a castigat cu ') + 1:]
        if 'Bidder' in id and ('bidders' not in self.stats or id not in self.stats['bidders']):
            if 'bidders' not in self.stats:
                self.stats['bidders'] = [id]
            else:
                self.stats['bidders'].append(id)
        if 'MessageProcessor' == id:
            if 'msg_proc_msg' not in self.stats:
                self.stats['msg_proc_msg'] = 0
            self.stats['msg_proc_msg'] += 1

    def monitor(self):
        # se preiau toate ofertele din topic
        print("Astept mesaje...")
        while True:
            while self.queue_msg == '':
                self.rabbit.receive_message()
            print(self.queue_msg)
            msg = self.queue_msg
            self.queue_msg = ''
            identity = msg.split('~')[0]
            timestamp = msg.split('~')[1]
            msg_content = msg.split('~')[2]
            self.gather_stat(msg_content, timestamp, identity)
            if identity == 'BiddingProcessor' and 'a castigat' in msg_content:
                break

        # check for end of auction message?
        self.end_of_bid()

    def run(self):
        self.monitor()


if __name__ == '__main__':
    monitor = Monitor(
        monitor_topic="topic_monitor",
        log_file="logs.txt"
    )
    monitor.run()
