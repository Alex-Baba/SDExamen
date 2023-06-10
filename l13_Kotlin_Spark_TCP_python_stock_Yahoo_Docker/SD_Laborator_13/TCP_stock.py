import socket
import requests
import json
from datetime import date
import time

today = date.today()
print("Today's date:", today)

HOST = '0.0.0.0'
PORT = 9999

API_KEY = 'brmr2kfrh5rcss140jmg'
STOCK_URL = 'https://finnhub.io/api/v1/'

def get_company_details(company):
    json_resp = requests.get(f'{STOCK_URL}company-news?symbol={company}&from={today}&to={today}&token={API_KEY}').json()
    return json_resp

if __name__ == '__main__':
    # get list of companies
    json_resp = requests.get(f'{STOCK_URL}stock/symbol?exchange=US&token={API_KEY}').json()
    companies = []
    for company in json_resp:
        companies.append(company['symbol'])
    print(len(companies))
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind((HOST,PORT))
        print('Ready to accept conections')
        while True:
            sock.listen()
            conn, addr = sock.accept()
            with conn:
                try:
                    print(f'Connected with {addr}')
                    # send stuff
                    # get companies that actually have news
                    for company in companies:
                        if conn.fileno() == -1:
                            break
                        print(company)
                        comp_details = get_company_details(company)
                        if len(comp_details) == 0:
                            continue
                        for news in comp_details:
                            json_d = json.dumps(news)
                            print(json_d)
                            conn.sendall(bytes(f'{json_d}\n', 'utf-8'))
                            time.sleep(3)
                except Exception as e:
                    print(e)
                    print(f'Disconnected {addr}')
        
        
        
