FROM python:alpine

# instalare modul kafka-python
RUN pip install kafka-python
RUN pip install pika==1.1.0 retry==0.9.2

# adaugare fisier sursa pentru microserviciul Auctioneer
ADD ErrorMonitor.py ErrorMonitor.py

# comanda de executie este: python <nume_fisier>.py
CMD ["python","-u", "ErrorMonitor.py"]
