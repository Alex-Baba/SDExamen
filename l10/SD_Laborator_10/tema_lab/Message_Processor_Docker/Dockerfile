FROM python:alpine

# instalare modul kafka-python
RUN pip install kafka-python

# adaugare fisier sursa pentru microserviciul MessageProcessor
WORKDIR /auctioneer
ADD MessageProcessor.py $WORKDIR

# comanda de executie este: python <nume_fisier>.py
CMD ["python", "MessageProcessor.py"]
