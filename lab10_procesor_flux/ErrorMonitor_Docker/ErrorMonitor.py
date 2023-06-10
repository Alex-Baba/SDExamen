import pika
from retry import retry


class RabbitMq:

    config = {
        'host': '0.0.0.0',
        'port': 5678,
        'username': 'student',
        'password': 'student',
        'exchange': 'errdetector.input',
        'routing_key': 'errdetector.outputKey',
        'queue': 'errdetector.input'
    }

    file = None

    credentials = pika.PlainCredentials(config['username'], config['password'])
    parameters = (pika.ConnectionParameters(host=config['host']),
                  pika.ConnectionParameters(port=config['port']),
                  pika.ConnectionParameters(credentials=credentials))


    def on_received_message(self, blocking_channel, deliver, properties ,message):
        result = message.decode('utf-8')

        blocking_channel.confirm_delivery()
        blocking_channel.basic_ack(deliver.delivery_tag)

        try:
            print(result)
            self.process_result(result)

        except Exception:
            print("wrong data format")
        finally:
            blocking_channel.stop_consuming()

    def process_result(self,result):
        self.file = open("log.txt", "a")

        sender, command, message  = result.rsplit('~',3)

        print(sender, command, message )

        if(command == 'error'):
            self.file.write("Microservice " + sender + " has error " + message)
            self.file.write('\n')

        if (command == 'command' and sender == 'actioneer'):
            self.file.write("This  auction has ended.\n")

        self.file.close()

    @retry(pika.exceptions.AMQPConnectionError, delay=5, jitter=(1, 3))
    def receive_message(self):
        # automatically close the connection
        with pika.BlockingConnection(self.parameters) as connection:
            # automatically close the channel
            with connection.channel() as channel:
                channel.basic_consume( self.config['queue'], self.on_received_message)
                try:
                    channel.start_consuming()
                # Don't recover connections closed by server
                except pika.exceptions.ConnectionClosedByBroker:
                    print("Connection closed by broker.")
                # Don't recover on channel errors
                except pika.exceptions.AMQPChannelError:
                    print("AMQP Channel Error")
                # Don't recover from KeyboardInterrupt
                except KeyboardInterrupt:
                    print("Application closed.")

    def send_message(self, message):
        # automatically close the connection
        with pika.BlockingConnection(self.parameters) as connection:
            # automatically close the channel
            with connection.channel() as channel:
                self.clear_queue(channel)
                channel.basic_publish(exchange=self.config['exchange'],
                                      routing_key=self.config['routing_key'],
                                      body=message)

    def clear_queue(self, channel):
        channel.queue_purge(self.config['queue'])

print("We started.")

if __name__ == '__main__':
    print("We started looking for errors.")
    rabbit_mq = RabbitMq()
    while True:
        rabbit_mq.receive_message()
