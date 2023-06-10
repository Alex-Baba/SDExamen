FROM python:alpine

# instalare modul kafka-python
RUN pip install kafka-python

# adaugare fisier sursa pentru microserviciul BiddingProcessor
WORKDIR /biddingProcessor
ADD BiddingProcessor.py $WORKDIR

# comanda de executie este: python <nume_fisier>.py
CMD ["python", "BiddingProcessor.py"]
