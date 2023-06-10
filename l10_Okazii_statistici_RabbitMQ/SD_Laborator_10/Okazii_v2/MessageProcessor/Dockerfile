FROM python:alpine

# instalare modul kafka-python
RUN pip install kafka-python
RUN pip install pika==1.1.0
RUN pip install retry==0.9.2
# adaugare fisier sursa pentru microserviciul Auctioneer
#WORKDIR /auctioneer
ADD ./Okazii_v2/MessageProcessor/MessageProcessor.py MessageProcessor.py
ADD ./Okazii_v2/MessageProcessor/mq_communication.py mq_communication.py
# ENV START_VALUE=20000 AUC_TYPE="English"
# comanda de executie este: python <nume_fisier>.py
CMD ["python", "MessageProcessor.py"]
