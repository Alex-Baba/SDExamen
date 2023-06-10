import json
import flask
import pika

app = flask.Flask(__name__)
userKey = ""
EXCHANGE = 'lab11'
ROUTING_KEY = 'incrementQueue'
credentials = pika.PlainCredentials('student','student')
parameters = (pika.ConnectionParameters(host='0.0.0.0'),pika.ConnectionParameters(port=5678),pika.ConnectionParameters(credentials=credentials))

def send_message(message):
# automatically close the connection
    with pika.BlockingConnection(parameters) as connection:# automatically close the channel
        with connection.channel() as channel:
            #self.clear_queue(channel)
            channel.basic_publish(
            exchange=EXCHANGE,
            routing_key=ROUTING_KEY,
            body=message)

@app.route("/")
def index():
    return flask.render_template("index.html")


@app.route("/increment", methods=['POST'])
def send_increment_event():
    print('post')
    json_body = json.loads(flask.request.data.decode(encoding='utf-8'))
    print(json_body['id'])
    send_message(json_body['id'])
    return '', 204

if __name__ == '__main__':
    app.run(debug=True)
