from audioop import add
import re
from tkinter import *
from tkinter import ttk
import threading
import socket

HOST = "localhost"
REGISTRAR_PORT = 1500
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

def register():
    sock.connect((HOST, REGISTRAR_PORT))
    sock.send(bytes('sub~{"name":"ui","host":"localhost","port":"-1"}\n', 'utf-8'))


def resolve_question(question_text):
    try:
        response_text = ""
        sock.send(bytes('find~teacher.*\n', 'utf-8'))
        response_msg = str(sock.recv(1024), "utf-8")
        print(response_msg)
        if '200' in response_msg:
            list = response_msg.split('~')[1].split(',')
            for address in list:
                print(address)
                host,port = address.split(":")
                newSock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                newSock.connect((host, int(port)))
                newSock.send(bytes(question_text + "\n", "utf-8"))
                response_text = str(newSock.recv(1024), "utf-8")
                if response_text != "":
                    break

        # transmitere intrebare - se deleaga intrebarea catre microserviciu
        #sock.send(bytes(question_text + "\n", "utf-8"))

        # primire raspuns -> microserviciul Teacher foloseste coregrafia de microservicii pentru a trimite raspunsul inapoi
 

    except ConnectionError:
        # in cazul unei erori de conexiune, se afiseaza un mesaj
        response_text = "Eroare de conectare la microserviciul Teacher!"

    # se adauga raspunsul primit in caseta text din interfata grafica
    response_widget.insert(END, response_text)


def ask_question():
    # preluare text intrebare de pe interfata grafica
    question_text = question.get()

    # pornire thread separat pentru tratarea intrebarii respective
    # astfel, nu se blocheaza interfata grafica!
    threading.Thread(target=resolve_question, args=(question_text,)).start()


if __name__ == '__main__':
    # elementul radacina al interfetei grafice
    root = Tk()
    root.title("Interactiune profesor-studenti")

    # la redimensionarea ferestrei, cadrele se extind pentru a prelua spatiul ramas
    root.columnconfigure(0, weight=1)
    root.rowconfigure(0, weight=1)

    # cadrul care incapsuleaza intregul continut
    content = ttk.Frame(root)

    # caseta text care afiseaza raspunsurile la intrebari
    response_widget = Text(content, height=10, width=50)

    # eticheta text din partea dreapta
    question_label = ttk.Label(content, text="Profesorul intreaba:")

    # caseta de introducere text cu care se preia intrebarea de la utilizator
    question = ttk.Entry(content, width=50)

    # butoanele din dreapta-jos
    ask = ttk.Button(content, text="Intreaba", command=ask_question)  # la apasare, se apeleaza functia ask_question
    exitbtn = ttk.Button(content, text="Iesi", command=root.destroy)  # la apasare, se iese din aplicatie

    # plasarea elementelor in layout-ul de tip grid
    content.grid(column=0, row=0)
    response_widget.grid(column=0, row=0, columnspan=3, rowspan=4)
    question_label.grid(column=3, row=0, columnspan=2)
    question.grid(column=3, row=1, columnspan=2)
    ask.grid(column=3, row=3)
    exitbtn.grid(column=4, row=3)
    register()
    # bucla principala a interfetei grafice care asteapta evenimente de la utilizator
    root.mainloop()
