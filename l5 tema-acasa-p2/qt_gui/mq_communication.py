import functools

import pika
from pika.adapters.asyncio_connection import AsyncioConnection
from pika.exchange_type import ExchangeType


class RabbitMq:
    config = {
        'host': '127.0.0.1',
        'port': 5672,
        'username': 'student',
        'password': 'student',
        'exchange': 'libraryapp.direct',
        'routing_key': 'libraryapp.routingkey1',
        'queue': 'libraryapp.queue'
    }
    credentials = pika.PlainCredentials(config['username'], config['password'])
    parameters = pika.ConnectionParameters(host=config['host'],
                                           port=config['port'],
                                           virtual_host='/python',
                                           socket_timeout=10.0,
                                           credentials=credentials)

    def __init__(self, ui):
        self.consumer_tag = None
        self.channel = None
        self.ui = ui
        self.connection = AsyncioConnection(
            parameters=RabbitMq.parameters,
            on_open_callback=self.on_connection_open,
            on_open_error_callback=self.on_connection_open_error)
        self.connection.ioloop.run_forever()

    def on_connection_open(self):
        self.connection.channel(on_open_callback=self.on_channel_open)

    def on_connection_open_error(self, _unused_connection, err):
        print('Connection open failed', err)
        self.stop()

    def on_channel_open(self, channel):
        self.channel = channel
        self.setup_exchange(RabbitMq.config['exchange'])

    def setup_exchange(self, exchange_name):
        cb = functools.partial(
            self.on_exchange_declareok, userdata=exchange_name)
        self.channel.exchange_declare(
            exchange=exchange_name,
            exchange_type=ExchangeType.direct,
            callback=cb)

    def on_exchange_declareok(self, _unused_frame, userdata):

        self.setup_queue(RabbitMq.config['queue'])

    def setup_queue(self, queue_name):
        cb = functools.partial(self.on_queue_declareok, userdata=queue_name)
        self.channel.queue_declare(queue=queue_name, callback=cb)

    def on_queue_declareok(self, _unused_frame, userdata):
        queue_name = userdata
        cb = functools.partial(self.on_bindok, userdata=queue_name)
        self.channel.queue_bind(
            queue_name,
            RabbitMq.config['exchange'],
            routing_key=RabbitMq.config['routing_key'],
            callback=cb)

    def on_bindok(self, _unused_frame, userdata):
        self.start_consuming()

    def start_consuming(self):
        self.consumer_tag = self.channel.basic_consume(
            RabbitMq.config['queue'], self.on_received_message)

    def on_received_message(self, _unused_channel, deliver, properties,
                            message):
        result = message.decode('utf-8')
        self.channel.basic_ack(deliver.delivery_tag)
        try:
            self.ui.set_response(result)
        except Exception as e:
            print(e)

    def stop_consuming(self):
        if self.channel:
            cb = functools.partial(
                self.on_cancelok, userdata=self.consumer_tag)
            self.channel.basic_cancel(self.consumer_tag, cb)

    def on_cancelok(self):
        self.channel.close()

    def stop(self):
        self.stop_consuming()
        self.connection.ioloop.stop()

    def send_message(self, message):
        self.channel.basic_publish(exchange=self.config['exchange'],
                                   routing_key=self.config['routing_key'],
                                   body=message)

    def clear_queue(self, channel):
        channel.queue_purge(self.config['queue'])
