import socket
import requests
import time
import json
from kafka import KafkaProducer

HOST = 'localhost'
PORT = 9999

STOCK_URL = 'https://finnhub.io/api/v1/company-news'
API_KEY = 'carc772ad3ib8nbu71ug'

if __name__ == '__main__':
    company  = "AAPL"
    fromDate = "2021-09-01"
    toDate = "2021-09-09"
    newsResponse = requests.get(f'{STOCK_URL}?symbol={company}&from={fromDate}&to={toDate}&token={API_KEY}').json()

    newsList = []

    for news in newsResponse:
        string = str(news['id'])+":"+news['summary']
        newsList.append(string)

    print(len(newsList))

    print(newsList)
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind((HOST, PORT))
        print('Astept conexiuni..')
        while True:
            sock.listen()
            conn, addr = sock.accept()
            with conn:
                try:
                    for newsItem in newsList:
                        print(newsItem)
                        conn.sendall(bytes(f'{newsItem}\n','utf-8') )
                        time.sleep(3)

                except Exception as e:
                    print(e)
                    #conn.close()