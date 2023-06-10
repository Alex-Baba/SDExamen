import socket
import requests
import time
import json

HOST = 'localhost'
PORT = 9999

STOCK_URL = 'https://finnhub.io/api/v1/stock/'
API_KEY = 'brl7eb7rh5re1lvco7fg'

# PRICE_TARGET E PREMIUM SI NU AVEM API KEY PREMIUM...DERCI MERGEM PE INCREDERE CA SCRIU COD BUN

if __name__ == '__main__':

    companies = requests.get(f'{STOCK_URL}symbol?exchange=US&token={API_KEY}').json()
    #print(companies)
    companies_symbol = []
    for comp in companies:
        companies_symbol.append(comp['symbol'])
    print(len(companies_symbol))

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind((HOST,PORT))
        print('Astept conexiuni..')
        while True:
            sock.listen()
            conn, addr = sock.accept()
            with conn:
                try:
                    for company in companies_symbol:
                        print(company)
                        company_target = requests.get(f'{STOCK_URL}price-target?symbol={company}&token={API_KEY}').json()
                        json_string = json.dumps(company_target)
                        print(json_string)
                        conn.sendal(bytes(f'{json_string}\n', 'utf-8'))
                        time.sleep(3)
                except:
                    conn.close()